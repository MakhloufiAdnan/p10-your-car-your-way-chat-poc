import { Component, DestroyRef, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize, Subscription } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/auth.service';
import { ChatService } from '../data-access/chat.service';
import { ChatRealtimeService } from '../data-access/chat-realtime.service';
import { ConversationSummary } from '../data-access/conversation-summary';
import { ChatMessage } from '../data-access/chat-message';
import { ConversationsListComponent } from '../ui/conversations-list/conversations-list.component';
import { MessagesListComponent } from '../ui/messages-list/messages-list.component';
import { MessageComposerComponent } from '../ui/message-composer/message-composer.component';

/**
* Orchestre la page principale du chat :
* - chargement des conversations,
* - chargement de l'historique,
* - abonnement temps réel,
* - envoi de message,
* - gestion des états de chargement et d'erreur.
*/
@Component({
  selector: 'app-chat-page',
  standalone: true,
  imports: [
    CommonModule,
    ConversationsListComponent,
    MessagesListComponent,
    MessageComposerComponent,
  ],
  templateUrl: './chat-page.component.html',
  styleUrl: './chat-page.component.scss',
})
export class ChatPageComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly chatService = inject(ChatService);
  private readonly chatRealtimeService = inject(ChatRealtimeService);
  private readonly destroyRef = inject(DestroyRef);

  private realtimeSubscription: Subscription | null = null;

  @ViewChild(MessageComposerComponent)
  private readonly messageComposer?: MessageComposerComponent;

  protected readonly currentUser = this.authService.getCurrentUser();
  protected readonly conversations = signal<ConversationSummary[]>([]);
  protected readonly selectedConversation = signal<ConversationSummary | null>(null);
  protected readonly messages = signal<ChatMessage[]>([]);

  protected readonly isLoadingConversations = signal(false);
  protected readonly isLoadingMessages = signal(false);
  protected readonly isSendingMessage = signal(false);

  protected readonly conversationsLoadError = signal('');
  protected readonly messagesLoadError = signal('');
  protected readonly sendMessageError = signal('');

  /**
  * Ouvre la connexion temps réel dès l'entrée sur la page,
  * puis charge les conversations accessibles à l'utilisateur connecté.
  */
  ngOnInit(): void {
    this.chatRealtimeService.connect();
    this.loadConversations();
  }

  /**
  * Change la conversation affichée et resynchronise à la fois :
  * - l'historique REST,
  * - l'abonnement WebSocket temps réel.
  */
  protected selectConversation(conversation: ConversationSummary): void {
    const currentSelectedId = this.selectedConversation()?.conversationId;

    if (currentSelectedId === conversation.conversationId) {
      return;
    }

    this.selectedConversation.set(conversation);
    this.sendMessageError.set('');
    this.loadMessages(conversation.conversationId);
    this.subscribeToConversation(conversation.conversationId);
  }

  protected retryLoadConversations(): void {
    this.loadConversations();
  }

  protected retryLoadMessages(): void {
    const conversationId = this.selectedConversation()?.conversationId;

    if (!conversationId) {
      return;
    }

    this.loadMessages(conversationId);
  }

  /**
  * Envoie un message via l'API REST.
  *
  * Le message est ensuite reçu en temps réel via WebSocket par les participants.
  * Un garde-fou anti-doublon évite d'afficher deux fois le même message.
  */
  protected submitMessage(content: string): void {
    const conversation = this.selectedConversation();

    if (!conversation) {
      this.sendMessageError.set('Aucune conversation sélectionnée.');
      return;
    }

    this.isSendingMessage.set(true);
    this.sendMessageError.set('');

    this.chatService
      .sendMessage(conversation.conversationId, { content })
      .pipe(
        finalize(() => this.isSendingMessage.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (createdMessage) => {
          this.appendMessageIfMissing(createdMessage);
          this.messageComposer?.reset();
        },
        error: () => {
          this.sendMessageError.set("Impossible d'envoyer le message.");
        },
      });
  }

  protected logout(): void {
    this.realtimeSubscription?.unsubscribe();
    this.chatRealtimeService.disconnect();
    this.authService.logout();
  }

  private loadConversations(): void {
    this.isLoadingConversations.set(true);
    this.conversationsLoadError.set('');

    this.chatService
      .getConversations()
      .pipe(
        finalize(() => this.isLoadingConversations.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (conversations) => {
          this.conversations.set(conversations);

          const currentSelectedId = this.selectedConversation()?.conversationId;

          const nextSelectedConversation =
            conversations.find(
              (conversation) => conversation.conversationId === currentSelectedId,
            ) ??
            conversations[0] ??
            null;

          this.selectedConversation.set(nextSelectedConversation);

          if (nextSelectedConversation) {
            this.loadMessages(nextSelectedConversation.conversationId);
            this.subscribeToConversation(nextSelectedConversation.conversationId);
            return;
          }

          this.messages.set([]);
          this.messagesLoadError.set('');
          this.sendMessageError.set('');
        },
        error: () => {
          this.conversations.set([]);
          this.selectedConversation.set(null);
          this.messages.set([]);
          this.conversationsLoadError.set('Impossible de charger les conversations.');
          this.messagesLoadError.set('');
          this.sendMessageError.set('');
        },
      });
  }

  private loadMessages(conversationId: string): void {
    this.isLoadingMessages.set(true);
    this.messagesLoadError.set('');
    this.sendMessageError.set('');

    this.chatService
      .getMessages(conversationId)
      .pipe(
        finalize(() => this.isLoadingMessages.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (messages) => {
          this.messages.set(messages);
        },
        error: () => {
          this.messages.set([]);
          this.messagesLoadError.set('Impossible de charger les messages.');
        },
      });
  }

  /**
  * Souscrit aux messages temps réel de la conversation actuellement sélectionnée.
  *
  * Le filtrage par `selectedConversationId` protège l'UI contre l'affichage
  * de messages arrivant alors que l'utilisateur a déjà changé de conversation.
  */
  private subscribeToConversation(conversationId: string): void {
    this.realtimeSubscription?.unsubscribe();

    this.realtimeSubscription = this.chatRealtimeService
      .watchConversation(conversationId)
      .subscribe((message) => {
        const selectedConversationId = this.selectedConversation()?.conversationId;

        if (message.conversationId !== selectedConversationId) {
          return;
        }

        this.appendMessageIfMissing(message);
      });
  }

  /**
  * Ajoute un message seulement s'il n'est pas déjà présent dans la liste.
  *
  * Cette protection évite les doublons possibles entre :
  * - la réponse HTTP après envoi,
  * - le push WebSocket reçu presque au même moment.
  */
  private appendMessageIfMissing(message: ChatMessage): void {
    this.messages.update((currentMessages) => {
      const alreadyExists = currentMessages.some(
        (currentMessage) => currentMessage.messageId === message.messageId,
      );

      if (alreadyExists) {
        return currentMessages;
      }

      return [...currentMessages, message];
    });
  }
}
