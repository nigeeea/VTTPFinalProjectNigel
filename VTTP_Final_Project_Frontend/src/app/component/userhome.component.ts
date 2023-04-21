import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecipeService } from '../recipe.service';
import {MatButtonModule} from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-userhome',
  templateUrl: './userhome.component.html',
  styleUrls: ['./userhome.component.css']
})
export class UserhomeComponent implements OnInit{

  emailLogged!:string | null

  constructor(private recipeSvc: RecipeService, private router:Router) {}

  ngOnInit(): void {

      //if not logged in deny access//
      console.info('email stored-->', localStorage.getItem('email'));
      this.emailLogged = localStorage.getItem('email');
      if(this.emailLogged === null){
        this.router.navigate(['/'])}
      //if not logged in deny access//
    }

    logOut(){
      this.recipeSvc.logOut()
      this.router.navigate(['/'])
  }
}