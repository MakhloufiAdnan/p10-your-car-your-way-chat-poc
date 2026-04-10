import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ChatMessage } from '../../data-access/chat-message';

@Component({
  selector: 'app-messages-list',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './messages-list.component.html',
  styleUrl: './messages-list.component.scss',
})
export class MessagesListComponent {
  readonly messages = input.required<ChatMessage[]>();
  readonly currentUsername = input.required<string>();
}
