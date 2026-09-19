import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { AccountService, Account } from './account.service';
import { CustomerService, Customer } from './customer.service';

@Component({
  imports: [DecimalPipe],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {
  protected readonly title = signal('Ebank Dashboard');
  protected readonly accounts = signal<Account[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly activeTab = signal<'accounts' | 'customers'>('accounts');

  constructor(
    private accountService: AccountService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: (data) => {
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les comptes. Vérifiez que les services tournent.');
        this.loading.set(false);
      }
    });

    this.customerService.getCustomers().subscribe({
      next: (data) => this.customers.set(data),
      error: () => {}
    });
  }

  showTab(tab: 'accounts' | 'customers'): void {
    this.activeTab.set(tab);
  }
}