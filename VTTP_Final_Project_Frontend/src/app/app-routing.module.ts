import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FavouritesComponent } from './component/favourites.component';
import { LoginComponent } from './component/login.component';
import { RegisterComponent } from './component/register.component';
import { SearchrecipeComponent } from './component/searchrecipe.component';
import { SinglefavComponent } from './component/singlefav.component';
import { StoreComponent } from './component/store.component';
import { UserhomeComponent } from './component/userhome.component';
import { UserprofileComponent } from './component/userprofile.component';

const routes: Routes = [
  {path:"", component: LoginComponent},
  {path:"registration", component: RegisterComponent},
  {path:"home", component: UserhomeComponent},
  {path: 'findarecipe', component: SearchrecipeComponent},
  {path:'savedrecipes', component: FavouritesComponent},
  {path:'profile', component: UserprofileComponent},
  {path:'store', component: StoreComponent},
  {path:'fav/:recipe_id', component: SinglefavComponent},
  { path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  // imports: [RouterModule.forRoot(routes)],
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
