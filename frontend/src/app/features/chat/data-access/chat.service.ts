import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ConversationSummary } from './conversation-summary';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getConversations(username: string): Observable<ConversationSummary[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<ConversationSummary[]>(`${this.apiUrl}/conversations`, {
      params
    });
  }
}
