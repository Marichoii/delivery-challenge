import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { interval, Subscription, switchMap } from 'rxjs';

type MenuItem = {
  id: string;
  name: string;
  price: number;
  restaurantId: string;
};

type Order = {
  id: string;
  customerId: string;
  restaurantId: string;
  itemId: string;
  itemName: string;
  price: number;
  status: string;
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  template: `
    <main>
      <header>
        <div>
          <span>Delivery Challenge</span>
          <h1>Cardapio</h1>
        </div>
        <button type="button" (click)="loadData()">Atualizar</button>
      </header>

      <section class="grid">
        <div class="panel">
          <h2>Itens</h2>
          <button
            class="item"
            type="button"
            *ngFor="let item of menu"
            [class.selected]="selectedItem?.id === item.id"
            (click)="selectItem(item)">
            <span>{{ item.name }}</span>
            <strong>{{ item.price | currency:'BRL' }}</strong>
          </button>
        </div>

        <aside class="panel">
          <h2>Novo pedido</h2>
          <div class="selected" *ngIf="selectedItem; else emptySelection">
            <strong>{{ selectedItem.name }}</strong>
            <span>{{ selectedItem.price | currency:'BRL' }}</span>
          </div>
          <ng-template #emptySelection>
            <p class="muted">Escolha um item do cardapio.</p>
          </ng-template>

          <button class="primary" type="button" [disabled]="!selectedItem || loading" (click)="createOrder()">
            Criar pedido
          </button>

          <p class="message" *ngIf="message">{{ message }}</p>
        </aside>
      </section>

      <section class="panel orders">
        <h2>Pedidos salvos</h2>
        <div class="order" *ngFor="let order of orders">
          <div>
            <strong>{{ order.itemName }}</strong>
            <span>{{ order.price | currency:'BRL' }}</span>
          </div>
          <span>{{ order.status }}</span>
        </div>
        <p class="muted" *ngIf="orders.length === 0">Nenhum pedido criado ainda.</p>
      </section>
    </main>
  `
})
class AppComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8081';
  private pollSub?: Subscription;

  menu: MenuItem[] = [];
  orders: Order[] = [];
  selectedItem?: MenuItem;
  loading = false;
  message = '';

  ngOnInit(): void {
    this.loadData();
    this.pollSub = interval(5000)
      .pipe(switchMap(() => this.http.get<Order[]>(`${this.apiUrl}/orders`)))
      .subscribe(orders => this.orders = orders);
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  loadData(): void {
    this.http.get<MenuItem[]>(`${this.apiUrl}/menu`).subscribe(menu => this.menu = menu);
    this.http.get<Order[]>(`${this.apiUrl}/orders`).subscribe(orders => this.orders = orders);
  }

  selectItem(item: MenuItem): void {
    this.selectedItem = item;
    this.message = '';
  }

  createOrder(): void {
    if (!this.selectedItem) return;

    this.loading = true;
    this.http.post<Order>(`${this.apiUrl}/orders`, {
      itemId: this.selectedItem.id
    }).subscribe({
      next: order => {
        this.orders = [...this.orders, order];
        this.message = 'Pedido criado e salvo no DB2.';
        this.loading = false;
      },
      error: () => {
        this.message = 'Nao foi possivel criar o pedido.';
        this.loading = false;
      }
    });
  }
}

bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()]
}).catch(error => console.error(error));
