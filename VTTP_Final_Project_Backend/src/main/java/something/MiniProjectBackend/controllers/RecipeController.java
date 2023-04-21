package something.MiniProjectBackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import something.MiniProjectBackend.models.RecipeInstructions;
import something.MiniProjectBackend.models.User;
import something.MiniProjectBackend.models.UserProfile;
import something.MiniProjectBackend.services.RecipeService;

@RestController
@RequestMapping(path = "/api")
public class RecipeController {

    @Autowired RecipeService recipeSvc;
    
    //METHOD TO REGISTER/SIGNUP USER IN SQL - METHOD 1.5
    @PostMapping(path = "registersql")
    public ResponseEntity<String> registerUserSql(
        @RequestParam MultiValueMap<String, String> params
    ){

        // boolean registrationStatus = recipeSvc.registerUserSql(email, name, password);

        // JsonObject response = Json.createObjectBuilder().add("registered",registrationStatus).build();

        String email = params.getFirst("email");
        String password = params.getFirst("password");
        String full_name = params.getFirst("full_name");
        String contact_number = params.getFirst("contact_number");
        Integer.parseInt(contact_number);
        String address = params.getFirst("address");
        String postal_code = params.getFirst("postal_code");

        System.out.println("is it receving?? - "+email+" - "+postal_code);

        boolean result = recipeSvc.registerUserSql(email, password, full_name, contact_number, address, postal_code);

        try {
            if(result){
            recipeSvc.sendEmail(email, "Food Finder Registered", "Hello "+full_name+". Welcome to the Food Finder community!");
            }
        } catch (Exception e) {
            System.out.println("Email Sending Error>>> "+e.getMessage());
        }
        
        //generate a jwt token for the new user
        String token = recipeSvc.generateJWT(email);

        JsonObject response = Json.createObjectBuilder()
                                    .add("registered", result)
                                    .add("token", token)
                                    .build();

        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response.toString());
    }


    //METHOD TO AUTHENTICATE USER LOGIN - METHOD 2
    @PostMapping(path = "/userAuth")
    public ResponseEntity<String> authenticateUser(
        @RequestParam String email,
        @RequestParam String password
    ){

        //create the user and send to the db
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        String token = "";

        boolean authenticationResponse = recipeSvc.authenticateUser(user);

        if(authenticationResponse){
            token = recipeSvc.generateJWT(email);
            System.out.println(token);
        }


        //note that the response is a boolean not string
        JsonObject response = Json.createObjectBuilder().add("authenticated", authenticationResponse)
                                                        .add("token", token)
                                                        .build();

        //if false then angular will tell user the usernmae or password is wrong
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }


    @PostMapping(path = "/saveRecipeTwo")
    public ResponseEntity<String> saveRecipeTwo(
        @RequestParam MultiValueMap<String, String> params,
        @RequestBody RecipeInstructions recipeInstructions,
        @RequestHeader String Authorization
    ){

        System.out.println("token in saveRecipeTwo==="+Authorization);
        //Verify JWT Token
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
        }

        //print out values to see if they pass through
        System.out.println(params.getFirst("email"));
        System.out.println(params.getFirst("recipe_id"));
        System.out.println(params.getFirst("recipe_name"));
        System.out.println(params.getFirst("image"));
        System.out.println(params.getFirst("url"));
        System.out.println(params.getFirst("calories"));
        System.out.println(params.getFirst("cuisine"));
        // System.out.println(params.getFirst("ingredients"));
        // System.out.println(params.getFirst("steps"));
        System.out.println(recipeInstructions.getIngredients()[0]);
        System.out.println(recipeInstructions.getSteps()[0]);

        //store the values in the appropriate data type
        Integer recipe_id = Integer.parseInt(params.getFirst("recipe_id"));
        String recipe_name = params.getFirst("recipe_name");
        String image = params.getFirst("image");
        String url = params.getFirst("url");
        Integer calories = Integer.parseInt(params.getFirst("calories"));
        String email = params.getFirst("email");
        String cuisine = params.getFirst("cuisine");

        //now create the method to save into sql and mongo omg it works wtfffff!!!!!
        boolean results = recipeSvc.saveRecipeTwo(recipeInstructions, recipe_id, recipe_name, image, url, calories, email, cuisine);
        // boolean results = false;
        JsonObject response = Json.createObjectBuilder().add("saved", results).build();

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @GetMapping(path = "/getFavourites")
    public ResponseEntity<String> getFavourites(
        @RequestParam String email,
        @RequestHeader String Authorization){

        System.out.println("this the token>>> "+Authorization+"dsffsdfsef");
        

        //Verify JWT Token
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
            // JsonObject response = Json.createObjectBuilder().add("error", "invalid token bro").build();
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            //                     .contentType(MediaType.APPLICATION_JSON)
            //                     .body(response.toString());
        }

        JsonArray response = recipeSvc.getFavourites(email);
        

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }


    @GetMapping(path = "/getUserProfile" )
    public ResponseEntity<String> getUserProfile(
        @RequestParam String email,
        @RequestHeader String Authorization
    ){

        System.out.println("get user profile token"+Authorization);
        //Verify JWT Token
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
        }

        JsonObject response = recipeSvc.getUserProfile(email);

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @PutMapping(path = "/editUserProfile")
    public ResponseEntity<String> editUserProfile(
        @RequestParam MultiValueMap<String, String> params,
        @RequestHeader String Authorization
    ){

        System.out.println("token in edit user profile===="+ Authorization);
        //Verify JWT Token
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
        }

        UserProfile userProfile = new UserProfile();

        userProfile.setAddress(params.getFirst("address"));
        userProfile.setContact_number(Integer.parseInt(params.getFirst("contact_number")));
        userProfile.setEmail(params.getFirst("email"));
        userProfile.setFull_name(params.getFirst("full_name"));
        userProfile.setPostal_code(Integer.parseInt(params.getFirst("postal_code")));

        System.out.println(userProfile.getAddress());
        System.out.println(userProfile.getEmail());
        System.out.println(userProfile.getFull_name());
        System.out.println(userProfile.getContact_number());
        System.out.println(userProfile.getPostal_code());
        

        //should also get one more param to see if email has been edited
        //if edited then use the method to update all tables
        //if not just use the method to update the 'contact' table
        String oldEmail = params.getFirst("oldEmail");
        System.out.println(oldEmail);

        if(oldEmail.equals(userProfile.getEmail())){

            boolean result = recipeSvc.updateUserProfile(userProfile);

            JsonObject response = Json.createObjectBuilder().add("profile_update", result).build();

            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
        }

        else{

            //create the method to update all the tables in teh database with the new email
            boolean result = recipeSvc.updateEmailAndUserProfile(userProfile, oldEmail);

            JsonObject response = Json.createObjectBuilder().add("profile_update", result).build();

            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
        }

    }

    @DeleteMapping(path = "/deleteRecipe")
    public ResponseEntity<String> deleteRecipe(
        @RequestParam String email,
        @RequestParam String recipe_id,
        @RequestHeader String Authorization
    ){

        //validate token
        System.out.println("In deleteRecipe==="+Authorization);
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
        }

        //create method to delete from recipe table
        Integer the_recipe_id = Integer.parseInt(recipe_id);

        boolean result = recipeSvc.deleteRecipe(email, the_recipe_id);

        JsonObject response = Json.createObjectBuilder().add("recipe_deleted", result).build();

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @GetMapping(path = "/searchRecipe")
    public ResponseEntity<String> getTheWholeRecipe(
        @RequestParam(defaultValue = "spanish") String cuisine,
        @RequestParam(defaultValue = "700") String calories,
        @RequestHeader String Authorization
    ){
        //validate token
        System.out.println("SearchRecipes Token ==== "+Authorization);
        if(!recipeSvc.verifyJWT(Authorization)){
            return recipeSvc.jwtInvalidMessage();
        }

        JsonObject response = recipeSvc.getRecipeTwo(cuisine, calories);

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @GetMapping(path = "/{recipe_id}")
    public ResponseEntity<String> getSingleRecipeInstructions(
        @PathVariable String recipe_id
    ){

        JsonObject response = recipeSvc.getSingleRecipeInstructions(recipe_id);

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @GetMapping(path = "/keytest")
    public ResponseEntity<String> getKey(
        @RequestParam String email
    ){

        String response = recipeSvc.generateJWT(email);
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }


    @GetMapping(path="/testkey")
    public ResponseEntity<String> testKeyAgain(
        @RequestParam String token
    ){

        boolean results = recipeSvc.verifyJWTBackend(token);
        JsonObject response = Json.createObjectBuilder().add("result", results).build();
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response.toString());
    }

    @GetMapping(path = "/teleGet")
    public ResponseEntity<String> teleGetRecipeNames(
        @RequestParam String email
    ){
        String response = recipeSvc.getTeleFavourites(email);

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response); 
    }
}
