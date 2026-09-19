import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { AccountService, Account } from './account.service';

@Component({
  imports: [DecimalPipe],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {
  protected readonly title = signal('Ebank Dashboard');
  protected readonly accounts = signal<Account[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: (data) => {
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Impossible de charger les comptes. Vérifiez que les services tournent.');
        this.loading.set(false);
      }
    });
  }
}