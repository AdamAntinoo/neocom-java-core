import { Component, OnInit } from '@angular/core';
import { EveItem } from '../models/eve-item';
import { EveItemService } from '../services/eve-item.service';

@Component({
  selector: 'app-eve-item',
  templateUrl: './eve-item.component.html',
  styleUrls: ['./eve-item.component.css']
})
export class EveItemComponent implements OnInit {
  private item: EveItem;

  constructor(private eveItemService: EveItemService) { }

  ngOnInit() {
    this.eveItemService.getEveItem(8501)
      .subscribe(
      item => { console.log(this.item) },
      err => { console.log(err); }
      );
  }

}
