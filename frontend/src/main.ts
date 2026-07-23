import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { bootstrapApplication } from '@angular/platform-browser';

type MenuItem = {
  id: string;
  name: string;
  price: number;
};

type MenuCategory = {
  name: string;
  items: MenuItem[];
};

type Order = {
  id: string;
  customerName: string;
  itemId: string;
  itemName: string;
  quantity: number;
  total: number;
  status: string;
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, FormsModule],
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
          <section class="category" *ngFor="let category of menu">
            <h3>{{ category.name }}</h3>
            <button
              class="item"
              type="button"
              *ngFor="let item of category.items"
              [class.selected]="selectedItem?.id === item.id"
              (click)="selectItem(item)">
              <span>{{ item.name }}</span>
              <strong>{{ item.price | currency:'BRL' }}</strong>
            </button>
          </section>
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

          <label>
            Quantidade
            <input type="number" min="1" [(ngModel)]="quantity">
          </label>

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
            <span>{{ order.customerName }} - qtd. {{ order.quantity }}</span>
          </div>
          <div>
            <strong>{{ order.total | currency:'BRL' }}</strong>
            <span>{{ order.status }}</span>
          </div>
        </div>
        <p class="muted" *ngIf="orders.length === 0">Nenhum pedido criado ainda.</p>
      </section>
    </main>
  `
})
class AppComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8081';

  menu: MenuCategory[] = [];
  orders: Order[] = [];
  selectedItem?: MenuItem;
  quantity = 1;
  loading = false;
  message = '';

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.http.get<MenuCategory[]>(`${this.apiUrl}/menu`).subscribe(menu => this.menu = menu);
    this.http.get<Order[]>(`${this.apiUrl}/orders`).subscribe(orders => this.orders = orders);
  }

  selectItem(item: MenuItem): void {
    this.selectedItem = item;
    this.message = '';
  }

  createOrder(): void {
    if (!this.selectedItem || this.quantity < 1) {
      return;
    }

    this.loading = true;
    this.http.post<Order>(`${this.apiUrl}/orders`, {
      itemId: this.selectedItem.id,
      quantity: this.quantity
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
