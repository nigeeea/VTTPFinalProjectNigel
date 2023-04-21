import { Injectable} from '@angular/core'
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http'
import{ firstValueFrom} from 'rxjs'
import { AuthenticationResult, DeleteRecipeResult, Recipe, RecipeInstructions, RegisterUserResult, SaveRecipeResult, UserProfile, UserProfileUpdateResult } from './models';
import { Router } from '@angular/router';

const BACKEND = 'https://foodfinderbynigel.up.railway.app'
// const BACKEND = 'http://localhost:8085';

@Injectable()
export class RecipeService{
    
    theUser!:string;
    favourites!: Recipe[]

    constructor(private http: HttpClient, private router: Router){}


    //METHOD TO AUTHENTICATE USER/LOGIN -- MYSQL
    autheticateUser(email: string, password: string) :Promise<AuthenticationResult>{

        const params = new HttpParams().set("email", email).set("password", password)
        //storing username in local storage                                
        this.theUser=email;
        //localStorage.setItem('email', email)
        
        return firstValueFrom(
            this.http.post<AuthenticationResult>(`${BACKEND}/api/userAuth`, params )
        )
        .then(results =>{
            console.info("authenticated??>>>", results);
            //this.authResult.next(results)
            return results;}
            )
    }


    //METHOD TO REGISTER USER -- MONGO OUTDATED
    registerUser(username: string, password: string): Promise<RegisterUserResult>{
        const params = new HttpParams().set("username", username).set("password", password);

        return firstValueFrom(
            this.http.post<RegisterUserResult>(`${BACKEND}/api/register`, params )
        )
        .then(results=>{
            console.info("registered???>>>", results);

            return results;
        })
    }


    //METHOD TO REGISTER USER -- MYSQL
    registerUsersql(email: string, 
                    password: string, 
                    full_name: string, 
                    contact_number: string,
                    address: string,
                    postal_code: string): Promise<RegisterUserResult>{
        const params = new HttpParams().set("email", email)
                                        .set("password", password)
                                        .set("full_name", full_name)
                                        .set("contact_number", contact_number)
                                        .set("address", address)
                                        .set("postal_code", postal_code);

        return firstValueFrom(
            this.http.post<RegisterUserResult>(`${BACKEND}/api/registersql`, params )
        )
        .then(results=>{
            console.info("registered???>>>", results);

            return results;
        })
    }

    
    //METHOD TO SEARCH RECIPE
    searchRecipe(cuisine: string, calories: string): Promise<Recipe>{

        const params = new HttpParams().set("cuisine", cuisine).set("calories", calories);

        let token: any = localStorage.getItem('token');
        
        const headers = new HttpHeaders().set("Authorization", "Bearer "+token);
        
        return firstValueFrom(
            this.http.get<Recipe>(`${BACKEND}/api/searchRecipe`, { headers: headers, params: params } )
        )
        .then(results => {
            console.info("in service--recipe???>>>", results);
            return results;
        }
        )
    }

    //METHOD TO SAVE SEARCHED RECIPE IMPROVED
    saveRecipeTwo(email: string,
            recipe_id: string,
            recipe_name: string,
            image: string,
            url: string,
            calories: string,
            cuisine: string,
            recipeInstructions: RecipeInstructions): Promise<SaveRecipeResult>{

    const params = new HttpParams().set("email", email)
                                    .set("recipe_id",recipe_id)
                                    .set("recipe_name",recipe_name)
                                    .set("image",image)
                                    .set("url",url)
                                    .set("calories",calories)
                                    .set("cuisine", cuisine);
    const body = recipeInstructions

    let token: any = localStorage.getItem('token');
        
    const headers = new HttpHeaders().set("Authorization", "Bearer "+token);
        
   return firstValueFrom(
        this.http.post<SaveRecipeResult>(`${BACKEND}/api/saveRecipeTwo`, body, { headers: headers, params: params })
        // this.http.post<SaveRecipeResult>(`${BACKEND}/api/saveRecipeTwo`, body, {params})
    )
    .then(results=>{
        console.info("in service--recipe Saved??>>", results);

        return results;
    }) 
}


    //METHOD TO GET FAVOURITES
    getFavourites(email: string, token: string): Promise<Recipe[]>{

        const params = new HttpParams().set("email", email);

        const headers = new HttpHeaders().set("Authorization", "Bearer "+token);

        return firstValueFrom(
            this.http.get<Recipe[]>(`${BACKEND}/api/getFavourites`, { headers: headers, params: params } )
        )
        .then(results=>{
            this.favourites=results;
            console.info(results);
            return results;
        })
    }

    //METHOD TO GET USER PROFILE DETAILS
    getUserProfile(email: string): Promise<UserProfile>{

        const params = new HttpParams().set("email", email);

        let token: any = localStorage.getItem('token');
        
        const headers = new HttpHeaders().set("Authorization", "Bearer "+token);

        return firstValueFrom(
            //this.http.get<UserProfile>(`${BACKEND}/api/getUserProfile`, {params} )
            this.http.get<UserProfile>(`${BACKEND}/api/getUserProfile`, { headers: headers, params: params } )
        )
        .then(results=>{
            console.info(results);
            return results;
        })
    }

    //METHOD TO UPDATE USER PROFILE DETAILS
    updateUserProfile(email: string,
                        full_name: string,
                        contact_number: number,
                        address: string,
                        postal_code: number,
                        oldEmail: string):Promise<UserProfileUpdateResult>{


        const params = new HttpParams().set("email", email)
                                        .set("full_name", full_name)
                                        .set("contact_number", contact_number)
                                        .set("address", address)
                                        .set("postal_code", postal_code)
                                        .set("oldEmail", oldEmail);

        let token: any = localStorage.getItem('token');
        
        const headers = new HttpHeaders().set("Authorization", "Bearer "+token);
        

        // https://stackoverflow.com/questions/60145885/angular-httpheaders-post-menthod-cant-set-parameters
        //second param for body, third param for options in put/post
        return firstValueFrom(
            this.http.put<UserProfileUpdateResult>(`${BACKEND}/api/editUserProfile`,{} ,{ headers: headers, params: params } )
            //this.http.put<UserProfileUpdateResult>(`${BACKEND}/api/editUserProfile`, params )
        )
        .then(results=>{
            console.info(results);
            window.location.reload();
            return results;
        })

        
    }


    //METHOD TO DELETE RECIPE FOR USER
    deleteRecipe(email: string, recipe_id: number): Promise<DeleteRecipeResult>{
        const params = new HttpParams().set("email", email).set("recipe_id", recipe_id);

        let token: any = localStorage.getItem('token');
        
        const headers = new HttpHeaders().set("Authorization", "Bearer "+token);

        return firstValueFrom(
            this.http.delete<DeleteRecipeResult>(`${BACKEND}/api/deleteRecipe`, { headers: headers, params: params } )
            //this.http.delete<DeleteRecipeResult>(`${BACKEND}/api/deleteRecipe`, {params} )
        )
        .then(results=>{
            console.info(results);
            return results;
        }
        )
    }

    //METHOD TO GET SINGLE RECIPE FOR INDIV FAVS PAGE
    getSingleRecipeInstructions(): Promise<RecipeInstructions>{

        return firstValueFrom(
            this.http.get<RecipeInstructions>(`${BACKEND}/api/`+localStorage.getItem('recipe_id'))
        )
        .then(results=>{
            console.info(results);
            return results;
        })
    }

    //LOGOUT METHOD - SHOULD BE IN EVERY COMPONENT TS AND BUTTON IN COMPONENT HTML NAVBAR
    logOut(){
        localStorage.removeItem('email')
        localStorage.removeItem('token')
        this.router.navigate(['/'])
    }

    favRecipeId!: number;
    favRecipeName!: string;
    favRecipeCalories!: number;
    favRecipeCuisine!: string;
    favRecipeImage!: string;
    //METHOD TO GO TO SINGLE FAV FROM FAVOURITES
    GoToSingleFav(recipe_id: number, recipe_name: string, calories: number, cuisine: string, image: string){
        this.favRecipeId=recipe_id;
        this.favRecipeName=recipe_name;
        this.favRecipeCalories=calories;
        this.favRecipeCuisine=cuisine;
        this.favRecipeImage=image;
        localStorage.setItem('recipe_id', String(recipe_id));
        localStorage.setItem('recipe_name',recipe_name)
        localStorage.setItem('calories',String(calories))
        localStorage.setItem('cuisine',cuisine)
        localStorage.setItem('image',image)

        this.router.navigate(['/fav',recipe_id])
    }

    //create method to add jwt token as header to the requests - maybe can just add in each method should be fine
    //https://stackoverflow.com/questions/65742101/how-to-get-jwt-token-from-response-header

}