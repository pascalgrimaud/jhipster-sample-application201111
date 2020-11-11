import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IToto } from 'app/shared/model/toto.model';

type EntityResponseType = HttpResponse<IToto>;
type EntityArrayResponseType = HttpResponse<IToto[]>;

@Injectable({ providedIn: 'root' })
export class TotoService {
  public resourceUrl = SERVER_API_URL + 'api/totos';

  constructor(protected http: HttpClient) {}

  create(toto: IToto): Observable<EntityResponseType> {
    return this.http.post<IToto>(this.resourceUrl, toto, { observe: 'response' });
  }

  update(toto: IToto): Observable<EntityResponseType> {
    return this.http.put<IToto>(this.resourceUrl, toto, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IToto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IToto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
