import { Component, DoCheck, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserProfile } from '../models';
import { RecipeService } from '../recipe.service';
import {FormGroup, FormBuilder, Validators} from '@angular/forms'
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-userprofile',
  templateUrl: './userprofile.component.html',
  styleUrls: ['./userprofile.component.css']
})
export class UserprofileComponent implements OnInit{

  userLogged!: string | null

  userProfile!:UserProfile

  toEdit: boolean = true;

  editProfileForm!:FormGroup

  constructor(private recipeSvc: RecipeService, private router:Router, private fb: FormBuilder) {}

  ngOnInit(): void {

    //if not logged in deny access//
    console.info('user stored-->', localStorage.getItem('email'));
    this.userLogged = localStorage.getItem('email');
    if(this.userLogged === null){
      this.router.navigate(['/'])}
    //if not logged in deny access//
    console.info('token stored profile-->', localStorage.getItem('token'));


    //get all the user profile and store in userProfile
      
    let email_address: string;

    if(this.userLogged === null) 
    {email_address = "noemail"}
    else{email_address = this.userLogged}

    //create function in service
    this.recipeSvc.getUserProfile(email_address)
    .then(results =>
      {console.info("returned>>",results);
        this.userProfile=results;
        console.info("This the profile>>", this.userProfile)
        console.info("this is the email>>", this.userProfile.email)
        
        this.editProfileForm=this.createForm(this.userProfile)
      return results;}
    )
    .catch(error=>
      {console.info("error", error)
      return error;}
      )
      
      console.info("before or after recipeSvc")
  }


  createForm(userProfile: UserProfile){
    return this.fb.group({
      email: this.fb.control({value: userProfile.email, disabled: this.toEdit}, [Validators.required]),
      full_name: this.fb.control({value: userProfile.full_name, disabled: this.toEdit}, [Validators.required]),
      contact_number: this.fb.control({value: userProfile.contact_number, disabled: this.toEdit}, [Validators.required]),
      address: this.fb.control({value: userProfile.address, disabled: this.toEdit}, [Validators.required]),
      postal_code: this.fb.control({value: userProfile.postal_code, disabled: this.toEdit}, [Validators.required]),
      something_else: this.fb.control({value: userProfile.email, disabled: this.toEdit}, [Validators.required])
    })
  }

  
  //edit button - when clicked the button will give the user the option to edit their details
  edit(){
    //https://stackoverflow.com/questions/42840136/disable-input-fields-in-reactive-form
    //this.form.controls['name'].disable();
    this.editProfileForm.controls['email'].enable()
    this.editProfileForm.controls['full_name'].enable()
    this.editProfileForm.controls['contact_number'].enable()
    this.editProfileForm.controls['address'].enable()
    this.editProfileForm.controls['postal_code'].enable()
    this.toEdit=false;
    
    // this.toEdit=false;
  }

  //the buttin for this form should be greyed out before the edit button is clicked
  submitForm(){
    const email = this.editProfileForm.get('email')?.value
    const full_name = this.editProfileForm.get('full_name')?.value
    const contact_number = this.editProfileForm.get('contact_number')?.value
    const address = this.editProfileForm.get('address')?.value
    const postal_code = this.editProfileForm.get('postal_code')?.value

    console.info('in component >>',email+full_name+contact_number+address+postal_code)
    
    let oldEmail: string = "";
    if(this.userLogged===null){oldEmail=""}
    else{oldEmail=this.userLogged}

    //create method in service to update the details -- but first have to check how
    // the backend method will look if user wants to change his email cos... data integrity or smth...
    this.recipeSvc.updateUserProfile(email, full_name, contact_number, address, postal_code, oldEmail)
    .then(results=>{
      console.info("is it updateD???", results)
      localStorage.setItem('email', email)
    })
    .catch(error=>{
      console.info("some error in updting", error)
    })

    //if result is true, rmbr to change the localStorage set email to the new email....
  }

  //in this component create a form to update profile information - refer to users and contact tables in mysql
  getEmail(){
    return this.userProfile.email
  }

  logOut(){
    this.recipeSvc.logOut()
    this.router.navigate(['/'])
}
}
