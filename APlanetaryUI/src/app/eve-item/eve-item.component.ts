import { Component, OnInit } from '@angular/core';
import { EveItem } from '../models/eve-item';
import { EveItemService } from '../services/eve-item.service';
import { Response } from '@angular/http';

@Component({
  selector: 'app-eve-item',
  templateUrl: './eve-item.component.html',
  styleUrls: ['./eve-item.component.css']
})
export class EveItemComponent implements OnInit {
  private itemResponse: Object = null;
  public item: EveItem;
  public downloaded: boolean = false;

  constructor(private eveItemService: EveItemService) { }

  ngOnInit() {
    this.eveItemService.getEveItem(16311)
      .subscribe(
      itemResponse => {
        // Do the actions of the completion of the subscription.
        this.item = this.createItem(itemResponse);
        this.downloaded = true;
        this.itemResponse;
      },
      err => { console.log(err); }
      );
    console.log(this.itemResponse)
    // Create a new EveItem from the response
  }
  public createItem(data): EveItem {
    this.item = new EveItem(data["name"]);
    // this.itemResponse["expanded"],
    //   this.itemResponse["downloaded"],
    //   this.itemResponse["renderWhenEmpty"],
    //   this.itemResponse["visible"],
    //   this.itemResponse["name"],
    //   this.itemResponse["category"],
    //   this.itemResponse["baseprice"],
    //   this.itemResponse["volume"],
    //   this.itemResponse["tech"],
    //   this.itemResponse["industryGroup"],
    //   this.itemResponse["groupName"],
    //   this.itemResponse["price"],
    //   this.itemResponse["typeID"],
    //   this.itemResponse["itemID"]);
    return this.item;
  }
}
