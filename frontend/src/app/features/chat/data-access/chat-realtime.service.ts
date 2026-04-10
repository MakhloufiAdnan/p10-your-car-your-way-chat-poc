import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ChatMessage } from './chat-message';

/**
* Gère la connexion STOMP/WebSocket du module de chat.
*
* Une seule connexion est maintenue côté frontend, puis l'abonnement est
* déplacé dynamiquement vers la conversation actuellement affichée.
*/
@Injectable({
  providedIn: 'root',
})
export class ChatRealtimeService {
  private client: Client | null = null;
  private activeConversationSubscription: StompSubscription | null = null;
  private isConnected = false;

  /**
  * Initialise le client STOMP une seule fois.
  *
  * La reconnexion automatique est activée pour rendre l'appli
  * plus robuste en cas de coupure réseau ou de redémarrage du backend.
  */
  connect(): void {
    if (this.client?.active || this.isConnected) {
      return;
    }

    this.client = new Client({
      brokerURL: environment.wsUrl,
      reconnectDelay: 5000,
      debug: (message) => console.debug('[STOMP]', message),
    });

    this.client.onConnect = () => {
      this.isConnected = true;
      console.log('[WS] connected');
    };

    this.client.onDisconnect = () => {
      this.isConnected = false;
      console.log('[WS] disconnected');
    };

    this.client.onStompError = (frame) => {
      console.error('[WS] STOMP error:', frame.headers['message'], frame.body);
    };

    this.client.onWebSocketError = (event) => {
      console.error('[WS] WebSocket error:', event);
    };

    this.client.activate();
  }

  /**
  * Change la conversation active observée en temps réel.
  *
  * Le service réutilise la même connexion WebSocket et remplace simplement
  * l'abonnement STOMP vers la file utilisateur correspondant à la conversation.
  */
  watchConversation(conversationId: string): Observable<ChatMessage> {
    return new Observable<ChatMessage>((subscriber) => {
      let retryHandle: ReturnType<typeof setTimeout> | null = null;

      const subscribeWhenReady = () => {
        if (!this.client || !this.isConnected) {
          retryHandle = setTimeout(subscribeWhenReady, 200);
          return;
        }

        this.activeConversationSubscription?.unsubscribe();

        this.activeConversationSubscription = this.client.subscribe(
          `/user/queue/conversations/${conversationId}`,
          (message: IMessage) => {
            console.log('[WS] message received:', message.body);
            subscriber.next(JSON.parse(message.body) as ChatMessage);
          },
        );
      };

      this.connect();
      subscribeWhenReady();

      return () => {
        if (retryHandle) {
          clearTimeout(retryHandle);
        }

        this.activeConversationSubscription?.unsubscribe();
        this.activeConversationSubscription = null;
      };
    });
  }

  disconnect(): void {
    this.activeConversationSubscription?.unsubscribe();
    this.activeConversationSubscription = null;

    if (this.client?.active) {
      void this.client.deactivate();
    }

    this.client = null;
    this.isConnected = false;
  }
}
