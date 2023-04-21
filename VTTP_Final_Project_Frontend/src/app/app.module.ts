import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {ReactiveFormsModule, FormsModule } from '@angular/forms';
import {HttpClientModule} from '@angular/common/http'

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/login.component';
//import { SignupComponent } from './component/signup.component';
import { UserhomeComponent } from './component/userhome.component';
import { RecipeService } from './recipe.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SearchrecipeComponent } from './component/searchrecipe.component';
import { RegisterComponent } from './component/register.component';
import { FavouritesComponent } from './component/favourites.component';
import { UserprofileComponent } from './component/userprofile.component';
import { StoreComponent } from './component/store.component';
import { SinglefavComponent } from './component/singlefav.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    //SignupComponent,
    UserhomeComponent,
    SearchrecipeComponent,
    RegisterComponent,
    FavouritesComponent,
    UserprofileComponent,
    StoreComponent,
    SinglefavComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    LeafletModule
  ],
  providers: [RecipeService],
  bootstrap: [AppComponent]
})
export class AppModule { }
