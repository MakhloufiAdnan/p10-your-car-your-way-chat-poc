import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ConversationSummary } from '../../data-access/conversation-summary';

@Component({
  selector: 'app-conversations-list',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './conversations-list.component.html',
  styleUrl: './conversations-list.component.scss'
})
export class ConversationsListComponent {
  readonly conversations = input.required<ConversationSummary[]>();
  readonly selectedConversationId = input<string | null>(null);
  readonly conversationSelected = output<ConversationSummary>();

  protected selectConversation(conversation: ConversationSummary): void {
    this.conversationSelected.emit(conversation);
  }

  protected isSelected(conversationId: string): boolean {
    return this.selectedConversationId() === conversationId;
  }
}
