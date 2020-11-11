import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IToto } from 'app/shared/model/toto.model';
import { TotoService } from './toto.service';
import { TotoDeleteDialogComponent } from './toto-delete-dialog.component';

@Component({
  selector: 'jhi-toto',
  templateUrl: './toto.component.html',
})
export class TotoComponent implements OnInit, OnDestroy {
  totos?: IToto[];
  eventSubscriber?: Subscription;

  constructor(protected totoService: TotoService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.totoService.query().subscribe((res: HttpResponse<IToto[]>) => (this.totos = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTotos();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IToto): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTotos(): void {
    this.eventSubscriber = this.eventManager.subscribe('totoListModification', () => this.loadAll());
  }

  delete(toto: IToto): void {
    const modalRef = this.modalService.open(TotoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.toto = toto;
  }
}
