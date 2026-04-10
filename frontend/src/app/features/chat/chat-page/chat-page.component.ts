import { Component, DestroyRef, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/auth.service';
import { ChatService } from '../data-access/chat.service';
import { ConversationSummary } from '../data-access/conversation-summary';
import { ChatMessage } from '../data-access/chat-message';
import { ConversationsListComponent } from '../ui/conversations-list/conversations-list.component';
import { MessagesListComponent } from '../ui/messages-list/messages-list.component';
import { MessageComposerComponent } from '../ui/message-composer/message-composer.component';

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
  private readonly destroyRef = inject(DestroyRef);

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

  ngOnInit(): void {
    this.loadConversations();
  }

  protected selectConversation(conversation: ConversationSummary): void {
    const currentSelectedId = this.selectedConversation()?.conversationId;

    if (currentSelectedId === conversation.conversationId) {
      return;
    }

    this.selectedConversation.set(conversation);
    this.sendMessageError.set('');
    this.loadMessages(conversation.conversationId);
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
          this.messages.update((messages) => [...messages, createdMessage]);
          this.messageComposer?.reset();
        },
        error: () => {
          this.sendMessageError.set("Impossible d'envoyer le message.");
        },
      });
  }

  protected logout(): void {
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
}
