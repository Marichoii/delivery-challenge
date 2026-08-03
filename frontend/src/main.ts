import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { EMPTY, Subscription, catchError, forkJoin, interval, switchMap } from 'rxjs';

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

type KanbanColumn = {
  status: string;
  label: string;
  color: string;
  next: string | null;
  nextLabel: string;
  actionColor: string;
};

const STATUS_LABEL: Record<string, string> = {
  CREATED: 'Recebido',
  CONFIRMED: 'Confirmado',
  PREPARING: 'Preparando',
  READY: 'Pronto',
  DELIVERED: 'Entregue',
};

const STATUS_COLOR: Record<string, string> = {
  CREATED: '#b45309',
  CONFIRMED: '#1d4ed8',
  PREPARING: '#7c3aed',
  READY: '#15803d',
  DELIVERED: '#374151',
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  template: `
    <div class="app">
      <header class="topbar">
        <div class="topbar-inner">
          <button class="brand" type="button" (click)="view = 'customer'">
            <span class="brand-mark">DC</span>
            <span>
              <span class="brand-name">Delivery Challenge</span>
              <span class="brand-subtitle">Pedido e operação</span>
            </span>
          </button>

          <nav class="tabs" aria-label="Visões do aplicativo">
            <button
              type="button"
              [class.active]="view === 'customer'"
              (click)="view = 'customer'">
              <span>Cardápio</span>
              <strong>{{ menu.length }}</strong>
            </button>
            <button
              type="button"
              [class.active]="view === 'restaurant'"
              (click)="view = 'restaurant'">
              <span>Restaurante</span>
              <strong>{{ activeOrders().length }}</strong>
            </button>
          </nav>
        </div>
      </header>

      <div class="notice" *ngIf="dataError">{{ dataError }}</div>

      <main class="content" *ngIf="view === 'customer'">
        <section class="page-head">
          <div>
            <span class="eyebrow">Cliente</span>
            <h1>Cardápio</h1>
            <p>Itens disponíveis para pedido agora.</p>
          </div>
          <button class="btn-secondary" type="button" [disabled]="refreshing" (click)="loadData()">
            {{ refreshing ? 'Atualizando...' : 'Atualizar' }}
          </button>
        </section>

        <div class="layout">
          <section class="menu-section" aria-labelledby="menu-title">
            <div class="section-head">
              <div>
                <h2 id="menu-title">Itens</h2>
                <span>{{ menu.length }} opções</span>
              </div>
            </div>

            <div class="empty-state" *ngIf="menu.length === 0">
              Sem itens no cardápio.
            </div>

            <div class="menu-grid" *ngIf="menu.length > 0">
              <button
                class="menu-card"
                type="button"
                *ngFor="let item of menu; trackBy: trackMenuItem"
                [class.selected]="selectedItem?.id === item.id"
                [attr.aria-pressed]="selectedItem?.id === item.id"
                [attr.aria-label]="'Selecionar ' + item.name"
                (click)="selectItem(item)">
                <span class="item-initial">{{ item.name.slice(0, 1) }}</span>
                <span class="item-copy">
                  <strong>{{ item.name }}</strong>
                  <small>{{ item.price | currency:'BRL' }}</small>
                </span>
                <span class="selected-dot" *ngIf="selectedItem?.id === item.id" aria-hidden="true"></span>
              </button>
            </div>
          </section>

          <aside class="order-panel" aria-labelledby="order-title">
            <div class="panel-title">
              <div>
                <h2 id="order-title">Novo pedido</h2>
                <span>{{ selectedItem ? 'Selecionado' : 'Aguardando item' }}</span>
              </div>
            </div>

            <div class="selection" *ngIf="selectedItem; else emptySelection">
              <span class="item-initial compact">{{ selectedItem.name.slice(0, 1) }}</span>
              <div>
                <strong>{{ selectedItem.name }}</strong>
                <small>{{ selectedItem.price | currency:'BRL' }}</small>
              </div>
            </div>
            <ng-template #emptySelection>
              <div class="empty-selection">
                <strong>Nenhum item selecionado</strong>
                <span>Selecione um item do cardápio para continuar.</span>
              </div>
            </ng-template>

            <button
              class="btn-primary"
              type="button"
              [disabled]="!selectedItem || loading"
              (click)="createOrder()">
              {{ loading ? 'Criando pedido...' : 'Confirmar pedido' }}
            </button>

            <div class="feedback success" *ngIf="message === 'ok'">
              Pedido criado com sucesso.
            </div>
            <div class="feedback error" *ngIf="message === 'error'">
              Não foi possível criar o pedido.
            </div>

            <section class="recent-orders" aria-labelledby="recent-title">
              <div class="section-head compact-head">
                <div>
                  <h3 id="recent-title">Pedidos recentes</h3>
                  <span>{{ orders.length }} no total</span>
                </div>
              </div>

              <div class="empty-list" *ngIf="orders.length === 0">Nenhum pedido ainda.</div>

              <div class="order-list" *ngIf="orders.length > 0">
                <div class="order-row" *ngFor="let order of recentOrders(); trackBy: trackOrder">
                  <div>
                    <strong>{{ order.itemName }}</strong>
                    <small>{{ order.price | currency:'BRL' }}</small>
                  </div>
                  <span class="status-pill" [style.border-color]="statusColor(order.status)" [style.color]="statusColor(order.status)">
                    {{ statusLabel(order.status) }}
                  </span>
                </div>
              </div>
            </section>
          </aside>
        </div>
      </main>

      <main class="content" *ngIf="view === 'restaurant'">
        <section class="page-head">
          <div>
            <span class="eyebrow">Restaurante</span>
            <h1>Operação</h1>
            <p>Pedidos por etapa de preparo.</p>
          </div>
          <button class="btn-secondary" type="button" [disabled]="refreshing" (click)="loadData()">
            {{ refreshing ? 'Atualizando...' : 'Atualizar' }}
          </button>
        </section>

        <section class="metrics" aria-label="Resumo de pedidos">
          <div class="metric">
            <strong>{{ orders.length }}</strong>
            <span>Pedidos</span>
          </div>
          <div class="metric">
            <strong>{{ activeOrders().length }}</strong>
            <span>Em andamento</span>
          </div>
          <div class="metric">
            <strong>{{ deliveredOrders().length }}</strong>
            <span>Entregues</span>
          </div>
          <div class="metric">
            <strong>{{ totalRevenue() | currency:'BRL' }}</strong>
            <span>Receita</span>
          </div>
        </section>

        <div class="kanban">
          <section class="kanban-col" *ngFor="let col of kanbanCols; trackBy: trackColumn">
            <div class="kanban-header" [style.border-color]="col.color">
              <div>
                <h2>{{ col.label }}</h2>
                <span>{{ ordersInStatus(col.status).length }} pedidos</span>
              </div>
              <span class="kanban-dot" [style.background]="col.color" aria-hidden="true"></span>
            </div>

            <div class="kanban-empty" *ngIf="ordersInStatus(col.status).length === 0">
              Fila vazia
            </div>

            <article class="kanban-card" *ngFor="let order of ordersInStatus(col.status); trackBy: trackOrder">
              <div class="kanban-card-top">
                <strong>{{ order.itemName }}</strong>
                <span>{{ order.price | currency:'BRL' }}</span>
              </div>
              <small class="order-id">#{{ order.id.slice(0, 8) }}</small>
              <button
                *ngIf="col.next"
                class="btn-action"
                type="button"
                [disabled]="updatingOrderId === order.id"
                [style.background]="col.actionColor"
                (click)="advance(order, col.next!)">
                {{ updatingOrderId === order.id ? 'Atualizando...' : col.nextLabel }}
              </button>
              <span class="done-label" *ngIf="!col.next">Finalizado</span>
            </article>
          </section>
        </div>
      </main>
    </div>
  `
})
class AppComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8081';
  private pollSub?: Subscription;

  view: 'customer' | 'restaurant' = 'customer';
  menu: MenuItem[] = [];
  orders: Order[] = [];
  selectedItem?: MenuItem;
  loading = false;
  refreshing = false;
  updatingOrderId = '';
  message: 'ok' | 'error' | '' = '';
  dataError = '';

  readonly kanbanCols: KanbanColumn[] = [
    { status: 'CREATED', label: 'Recebidos', color: '#b45309', next: 'confirm', nextLabel: 'Confirmar', actionColor: '#1d4ed8' },
    { status: 'CONFIRMED', label: 'Confirmados', color: '#1d4ed8', next: 'prepare', nextLabel: 'Preparar', actionColor: '#7c3aed' },
    { status: 'PREPARING', label: 'Preparando', color: '#7c3aed', next: 'ready', nextLabel: 'Marcar pronto', actionColor: '#15803d' },
    { status: 'READY', label: 'Prontos', color: '#15803d', next: 'deliver', nextLabel: 'Entregar', actionColor: '#374151' },
    { status: 'DELIVERED', label: 'Entregues', color: '#374151', next: null, nextLabel: '', actionColor: '' },
  ];

  ngOnInit(): void {
    this.loadData();
    this.pollSub = interval(5000)
      .pipe(
        switchMap(() => this.http.get<Order[]>(`${this.apiUrl}/orders`).pipe(
          catchError(() => EMPTY)
        ))
      )
      .subscribe(orders => this.orders = orders);
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  loadData(): void {
    this.refreshing = true;
    this.dataError = '';

    forkJoin({
      menu: this.http.get<MenuItem[]>(`${this.apiUrl}/menu`),
      orders: this.http.get<Order[]>(`${this.apiUrl}/orders`),
    }).subscribe({
      next: ({ menu, orders }) => {
        this.menu = menu;
        this.orders = orders;
      },
      error: () => {
        this.dataError = 'Não foi possível carregar os dados. Verifique se o backend está online.';
        this.refreshing = false;
      },
      complete: () => this.refreshing = false,
    });
  }

  selectItem(item: MenuItem): void {
    this.selectedItem = item;
    this.message = '';
  }

  createOrder(): void {
    if (!this.selectedItem) return;
    this.loading = true;
    this.message = '';
    this.dataError = '';

    this.http.post<Order>(`${this.apiUrl}/orders`, { itemId: this.selectedItem.id }).subscribe({
      next: order => {
        this.orders = [...this.orders, order];
        this.selectedItem = undefined;
        this.message = 'ok';
        this.loading = false;
        setTimeout(() => this.message = '', 3000);
      },
      error: () => {
        this.message = 'error';
        this.loading = false;
        setTimeout(() => this.message = '', 3000);
      }
    });
  }

  advance(order: Order, action: string): void {
    if (this.updatingOrderId) return;

    this.updatingOrderId = order.id;
    this.dataError = '';

    this.http.post<Order>(`${this.apiUrl}/orders/${order.id}/${action}`, {}).subscribe({
      next: updated => {
        this.orders = this.orders.map(o => o.id === updated.id ? updated : o);
      },
      error: () => {
        this.dataError = 'Não foi possível atualizar o pedido.';
        this.updatingOrderId = '';
      },
      complete: () => this.updatingOrderId = '',
    });
  }

  ordersInStatus(status: string): Order[] {
    return this.orders.filter(o => o.status === status);
  }

  statusLabel(status: string): string {
    return STATUS_LABEL[status] ?? status;
  }

  statusColor(status: string): string {
    return STATUS_COLOR[status] ?? '#374151';
  }

  recentOrders(): Order[] {
    return [...this.orders].slice(-5).reverse();
  }

  activeOrders(): Order[] {
    return this.orders.filter(order => order.status !== 'DELIVERED');
  }

  deliveredOrders(): Order[] {
    return this.orders.filter(order => order.status === 'DELIVERED');
  }

  totalRevenue(): number {
    return this.orders.reduce((total, order) => total + order.price, 0);
  }

  trackMenuItem(_index: number, item: MenuItem): string {
    return item.id;
  }

  trackOrder(_index: number, order: Order): string {
    return order.id;
  }

  trackColumn(_index: number, column: KanbanColumn): string {
    return column.status;
  }
}

bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()]
}).catch(error => console.error(error));
