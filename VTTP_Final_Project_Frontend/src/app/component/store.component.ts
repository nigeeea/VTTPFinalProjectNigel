import { Component, AfterViewInit, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecipeService } from '../recipe.service';
import * as L from 'leaflet';
import {Icon} from 'leaflet'

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent implements OnInit, AfterViewInit{

  // https://www.digitalocean.com/community/tutorials/angular-angular-and-leaflet 
  private map!:any;
  userLogged!: any;

  constructor(private recipeSvc: RecipeService, private router:Router) {}
  
  ngOnInit() {
    //if not logged in deny access//
    console.info('user stored-->', localStorage.getItem('email'));
    this.userLogged = localStorage.getItem('email');
    if(this.userLogged === null){
      this.router.navigate(['/'])}
    //if not logged in deny access//
    console.info('token stored-->', localStorage.getItem('token'));
  }


  ngAfterViewInit(): void {
  this.initMap();
  }
  
  

  private initMap(): void {
    this.map = L.map('map', {
      center: [ 1.29231, 103.77664 ],
      zoom: 17
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 20,
      minZoom: 14,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);
    // L.marker([1.29231, 103.77664]).addTo(this.map)
    L.circle([1.29210, 103.77647], {
      color: 'red',
      fillColor: '#f03',
      fillOpacity: 0.5,
      radius: 25
  }).addTo(this.map);

  }



  

  logOut(){
    this.recipeSvc.logOut()
    this.router.navigate(['/'])
}
}
