import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms'
import { RecipeService } from '../recipe.service';
import { Recipe, RecipeInstructions } from '../models';

@Component({
  selector: 'app-searchrecipe',
  templateUrl: './searchrecipe.component.html',
  styleUrls: ['./searchrecipe.component.css']
})
export class SearchrecipeComponent implements OnInit {

  recipeReceived!: Recipe 
  userLogged!:string | null
  tokenLogged!: string | null
  searchForm!:FormGroup
  saveRecipeForm!:FormGroup
  savedStatus!: string
  cuisineInput!:string

  constructor(private router: Router, private recipeSvc: RecipeService, private fb: FormBuilder){}

  ngOnInit(): void {
    //if not logged in deny access//
    console.info('user stored-->', localStorage.getItem('email'));
    this.userLogged = localStorage.getItem('email');
    if(this.userLogged === null){
      this.router.navigate(['/'])}
    //if not logged in deny access//
    console.info('token stored-->', localStorage.getItem('token'));


    this.searchForm = this.createForm()
  }

  createForm(){
    return this.fb.group({
      cuisine: this.fb.control('', [Validators.required]),
      calories: this.fb.control('', [Validators.required])
    })
  }

  submitForm(){
    const cuisine = this.searchForm.get('cuisine')?.value
    this.cuisineInput=cuisine
    const calories = this.searchForm.get('calories')?.value

    console.info('cuisine input>>', cuisine)
    console.info('calories input>>', calories)

    this.recipeSvc.searchRecipe(cuisine, calories)
    .then(results =>{
    this.recipeReceived=results;
    this.recipeReceived.cuisine=cuisine;
    // this.recipeInstructions.ingredients=results.ingredients;
    // this.recipeInstructions.steps=results.steps
    console.info('in component-->recipe',results);
  })
    .catch(error => {
      console.info("some searchRecipe error bruh>>>", error)
      //log the user out
      this.recipeSvc.logOut();
      return error;
    }

    )
  }

  //UPGRADED SAVE RECIPE METHOD
  saveRecipeTwo(){
    let email: string;
    if(this.userLogged === null){
      email = "noemail"
    }
    else{ email = this.userLogged}

    const recipe_id = String(this.recipeReceived.recipe_id)
    const recipe_name = this.recipeReceived.recipe_name
    const imageValue = this.recipeReceived.image
    const urlValue = this.recipeReceived.url
    const caloriesValue = String(this.recipeReceived.calories)
    const cuisine = this.cuisineInput
    const ingredients = this.recipeReceived.ingredients
    const steps = this.recipeReceived.steps

    let recipeIngredients: string[] = this.recipeReceived.ingredients;
    let recipeSteps: string[] = this.recipeReceived.steps;
    const recipeInstructions: RecipeInstructions = {steps: recipeSteps, ingredients: recipeIngredients};
    console.info("the steps in teh object>>>", recipeInstructions.steps);
    console.info('the ingredients in the object>>>', recipeInstructions.ingredients);
    console.info('the RecipeInstructions Object>>>', recipeInstructions);
    

    console.info('this is the id value>>>',recipe_id);
    console.info('this is the recipeName value>>>',recipe_name);
    console.info('this is the image value>>>',imageValue);
    console.info('this is the url value>>>',urlValue);
    console.info('this is the calories value>>>',caloriesValue);
    console.info('this is the cuisine>>>',cuisine);
    console.info('these are the ingredients>>>',recipeInstructions.ingredients);
    console.info('these are the steps>>>',recipeInstructions.steps);

    //now create a method in service to send the recipeInstructs object as a body and the other values as params
    //make sure that in spring boot backend there is a model object to receive/store the recipeInstructs object 
    this.recipeSvc.saveRecipeTwo(email,recipe_id,recipe_name,imageValue,urlValue,caloriesValue,cuisine, recipeInstructions)
    .then(results =>{
      console.info("in the component saveTwo-->",results);
      //make a ui response to tell the user that recipe has been saved
      if(results.saved){this.savedStatus="your recipe has been saved"}
      else{{this.savedStatus="error: your recipe has not been saved, try again later"}}
    })
    .catch(error => {
      console.info("some error bruh saveTwp>>>", error);
      //make an UI error to show user that the recipe is not saved
      this.savedStatus = "error: recipe is not saved"
    })
  }

  logOut(){
    this.recipeSvc.logOut()
    this.router.navigate(['/'])
  }

}