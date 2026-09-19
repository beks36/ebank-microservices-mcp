import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Account {
  id: number;
  rib: string;
  balance: number;
  customerId: number;
}

@Injectable({ providedIn: 'root' })
export class AccountService {
  private baseUrl = 'http://localhost:8888/api/accounts';

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.baseUrl);
  }
}