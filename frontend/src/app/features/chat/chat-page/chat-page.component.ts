import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ChatService } from '../data-access/chat.service';
import { ConversationSummary } from '../data-access/conversation-summary';
import { ConversationsListComponent } from '../ui/conversations-list/conversations-list.component';

@Component({
  selector: 'app-chat-page',
  standalone: true,
  imports: [CommonModule, ConversationsListComponent],
  templateUrl: './chat-page.component.html',
  styleUrl: './chat-page.component.scss',
})
export class ChatPageComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly chatService = inject(ChatService);

  protected readonly currentUser = this.authService.getCurrentUser();
  protected readonly conversations = signal<ConversationSummary[]>([]);
  protected readonly selectedConversation = signal<ConversationSummary | null>(null);
  protected readonly isLoadingConversations = signal(false);
  protected readonly loadError = signal('');

  ngOnInit(): void {
    this.loadConversations();
  }

  protected selectConversation(conversation: ConversationSummary): void {
    this.selectedConversation.set(conversation);
  }

  protected retryLoad(): void {
    this.loadConversations();
  }

  protected logout(): void {
    this.authService.logout();
  }

  private loadConversations(): void {
    this.isLoadingConversations.set(true);
    this.loadError.set('');

    this.chatService
      .getConversations()
      .pipe(finalize(() => this.isLoadingConversations.set(false)))
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
        },
        error: () => {
          this.conversations.set([]);
          this.selectedConversation.set(null);
          this.loadError.set('Impossible de charger les conversations.');
        },
      });
  }
}
