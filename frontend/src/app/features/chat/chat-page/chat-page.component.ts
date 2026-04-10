import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/auth.service';
import { ChatService } from '../data-access/chat.service';
import { ConversationSummary } from '../data-access/conversation-summary';
import { ChatMessage } from '../data-access/chat-message';
import { ConversationsListComponent } from '../ui/conversations-list/conversations-list.component';
import { MessagesListComponent } from '../ui/messages-list/messages-list.component';

@Component({
  selector: 'app-chat-page',
  standalone: true,
  imports: [CommonModule, ConversationsListComponent, MessagesListComponent],
  templateUrl: './chat-page.component.html',
  styleUrl: './chat-page.component.scss',
})
export class ChatPageComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly chatService = inject(ChatService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly currentUser = this.authService.getCurrentUser();
  protected readonly conversations = signal<ConversationSummary[]>([]);
  protected readonly selectedConversation = signal<ConversationSummary | null>(null);
  protected readonly messages = signal<ChatMessage[]>([]);

  protected readonly isLoadingConversations = signal(false);
  protected readonly isLoadingMessages = signal(false);

  protected readonly conversationsLoadError = signal('');
  protected readonly messagesLoadError = signal('');

  ngOnInit(): void {
    this.loadConversations();
  }

  protected selectConversation(conversation: ConversationSummary): void {
    const currentSelectedId = this.selectedConversation()?.conversationId;

    if (currentSelectedId === conversation.conversationId) {
      return;
    }

    this.selectedConversation.set(conversation);
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
        },
        error: () => {
          this.conversations.set([]);
          this.selectedConversation.set(null);
          this.messages.set([]);
          this.conversationsLoadError.set('Impossible de charger les conversations.');
          this.messagesLoadError.set('');
        },
      });
  }

  private loadMessages(conversationId: string): void {
    this.isLoadingMessages.set(true);
    this.messagesLoadError.set('');

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
