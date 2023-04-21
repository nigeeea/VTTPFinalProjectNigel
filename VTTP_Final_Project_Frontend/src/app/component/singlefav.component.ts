import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { RecipeService } from '../recipe.service';
import { Subscription } from 'rxjs';
import { RecipeInstructions } from '../models';

@Component({
  selector: 'app-singlefav',
  templateUrl: './singlefav.component.html',
  styleUrls: ['./singlefav.component.css']
})
export class SinglefavComponent implements OnInit {

  userLogged!:string | null
  favRecipeId!: number | null
  favRecipeName!: string | null
  favRecipeCalories!: number | null
  favRecipeCuisine!: string | null
  favRecipeImage!: string | null
  favTest$!: Subscription
  recipeInstructions!: RecipeInstructions
  
  constructor(private router: Router,private activatedRoute:ActivatedRoute, private recipeSvc: RecipeService, private fb: FormBuilder){}

  ngOnInit(): void {
    // //if not logged in deny access//
    // console.info('user stored-->', localStorage.getItem('email'));
    // this.userLogged = localStorage.getItem('email');
    // if(this.userLogged === null){
    //   this.router.navigate(['/'])}
    // //if not logged in deny access//

    //store the data so dont have to call from mysql
    this.favRecipeId=Number(localStorage.getItem('recipe_id'))
    this.favRecipeName=localStorage.getItem('recipe_name')
    this.favRecipeCalories=Number(localStorage.getItem('calories'))
    this.favRecipeCuisine=localStorage.getItem('cuisine')
    this.favRecipeImage=localStorage.getItem('image')

    //create method in service to pull instructions and ingredients from mongo
    this.recipeSvc.getSingleRecipeInstructions()
    .then(
      results=>{
        console.info(results);
        this.recipeInstructions=results;
      }
    )
    
  }




  goBack(){
    this.router.navigate(['/savedrecipes'])
  }

}
