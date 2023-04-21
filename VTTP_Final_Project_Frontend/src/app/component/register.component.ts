import { Component } from '@angular/core';
import {FormGroup, FormBuilder, Validators, AbstractControl, ValidatorFn} from '@angular/forms'
import { Router } from '@angular/router';
import { RecipeService } from '../recipe.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerForm!: FormGroup

  
  passWordStrength!: boolean


  constructor(private fb: FormBuilder, private recipeSvc: RecipeService, private router: Router){

    this.registerForm= this.createForm();

  }

  createForm(){
    return this.fb.group({
      email: this.fb.control('', [Validators.required, Validators.minLength(6)]),
      password: this.fb.control('', [Validators.required, Validators.minLength(6)]),
      passwordAgain: this.fb.control('',  [Validators.required, Validators.minLength(6), this.passwordMatchingValidator('password')]),
      full_name: this.fb.control(''),
      contact_number: this.fb.control(''),
      address: this.fb.control(''),
      postal_code: this.fb.control('')
    })
  }

  submitForm(){
    const email = this.registerForm.get('email')?.value
    const password = this.registerForm.get('password')?.value
    const passwordAgain = this.registerForm.get('passwordAgain')?.value
    const full_name = this.registerForm.get('full_name')?.value
    const contact_number = this.registerForm.get('contact_number')?.value
    const address = this.registerForm.get('address')?.value
    const postal_code = this.registerForm.get('postal_code')?.value

    console.info('user>>>',email)
    console.info('pass>>>',password)
    console.info('reentered pass>>>',passwordAgain)
    console.info('full_name', full_name)
    console.info('contact_number>>>', contact_number)
    console.info('address>>>', address)
    console.info('postal code>>>', postal_code)

    //USE THE SERVICE TO INSERT THE NEW USER INTO THE USERS MONGO COLLECTION
    this.recipeSvc.registerUsersql(email, password, full_name, contact_number, address, postal_code)
    .then(results =>{
      console.info(results)

      if(results.registered){
        localStorage.setItem("email", email)
        localStorage.setItem('token', results.token)
        this.router.navigate(['/home'])
      }
    })
    .catch(error => {
      console.info("some error bruh>>>", error)
    })

  }


  get username() {
    return this.registerForm.get('email')
  }

  get password() {
    return this.registerForm.get('password')
  }

  get passwordAgain() {
    return this.registerForm.get('passwordAgain')
  }

  //https://dzone.com/articles/how-to-create-custom-validators-in-angular
  //matchControlName is the value of the 'password' control
  //this function returns a validator function that will be used to match the values of 'passwordAgain' and 'password'
  passwordMatchingValidator(passwordInput: string): ValidatorFn 
  {
    return (control: AbstractControl): 
    //{ [key: string]: any } | null => 
    { [key: string]: boolean } | null =>
    {     
      const password = control.parent?.get(passwordInput)?.value

      const passwordAgain = control.value

      if(password === passwordAgain){return null}
      
      else{return {passwordMatch: true}}
    }
  }

}
