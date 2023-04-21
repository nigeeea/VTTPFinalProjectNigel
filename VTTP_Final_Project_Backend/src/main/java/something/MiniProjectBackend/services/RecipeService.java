package something.MiniProjectBackend.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import something.MiniProjectBackend.models.RecipeInstructions;
import something.MiniProjectBackend.models.User;
import something.MiniProjectBackend.models.UserProfile;
import something.MiniProjectBackend.repositories.RecipeRepository;

@Service
public class RecipeService {

    private static final String URL = "https://api.spoonacular.com/recipes/complexSearch";

    @Value("${API_KEY}")
    private String key;
    
    @Autowired RecipeRepository recipeRepo;

    //METHOD TO REGISTER/SIGNUP USER IN SQL -  METHOD 1
    public boolean registerUserSql(String email, 
                                String password, 
                                String full_name, 
                                String contact_number, 
                                String address, 
                                String postal_code){
        return recipeRepo.registerUserSql(email, password, full_name, contact_number, address, postal_code);
    }


    //METHOD TO AUTHENTICATE USER LOGIN - METHOD 2
    public boolean authenticateUser(User user){
        return recipeRepo.authenticateUser(user);
    }

    //METHOD TO SAVE RECIPE ENHANCED - METHOD 3.5
    public boolean saveRecipeTwo(RecipeInstructions recipeInstructions,
                                    Integer recipe_id,
                                    String recipe_name,
                                    String image,
                                    String url,
                                    Integer calories,
                                    String email,
                                    String cuisine ){

    return recipeRepo.SaveRecipeTwo(recipeInstructions, recipe_id, recipe_name, image, url, calories, email, cuisine);
    }

    //METHOD TO GET FAVOURITES - METHOD 4
    public JsonArray getFavourites(String email){
        return recipeRepo.getFavourites(email);
    }

    //METHOD TO GET USER PROFILE - METHOD 5
    public JsonObject getUserProfile(String email){
        return recipeRepo.getUserProfile(email);
    }

    //METHOD TO UPDATE USER PROFILE - METHOD 6
    public boolean updateUserProfile(UserProfile userProfile){
        return recipeRepo.updateUserProfile(userProfile);
    }

    //METHOD TO UPDATE USER PROFILE WITH EMAIL CHANGE - METHOD 6
    public boolean updateEmailAndUserProfile(UserProfile userProfile, String oldEmail){
        return recipeRepo.updateEmailAndUserProfile(userProfile, oldEmail);
    }

    //METHOD TO DELETE RECIPE FROM FAVOURITES - METHOD 7
    public boolean deleteRecipe(String email, Integer recipe_id){
        return recipeRepo.deleteRecipe(email, recipe_id);
    }

    //METHOD TO GET RECIPE INSTRUCTIONS FROM MONGO - METHOD 8
    public JsonObject getSingleRecipeInstructions(String recipe_id){
        return recipeRepo.getSingleRecipeInstructions(recipe_id);
    }

    //METHOD TO CALL API 2.0 UPGRADED
    public JsonObject getRecipeTwo(String cuisine, String calories){

    //hardset the number of results returned to 3 due to api call limit.. can increase the number later
        Integer numberOfResults = 7;

        String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("cuisine", cuisine)
        .queryParam("number", numberOfResults) //hardset to 7 due to API call limit
        .queryParam("maxCalories", calories)
        .queryParam("apiKey", key)
        .queryParam("instructionsRequired", "true")
        .queryParam("addRecipeInformation", "true")
        .toUriString();

        //create the GET Request
        RequestEntity<Void> req = RequestEntity.get(url).build();
        //Make the call to the API
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        //Check the response
        System.out.println(resp.getStatusCode());
        String payload = resp.getBody();
        System.out.println(payload);
        //convert the payload into a JSONObject
        Reader myStrReader = new StringReader(payload);
        JsonReader myJsonReader = Json.createReader(myStrReader);
        JsonObject initialJsonObject = myJsonReader.readObject();

        JsonArray initialJsonArray = initialJsonObject.getJsonArray("results");

        //this method is to get a random recipe out of the results returned (can get from the size of the JSON Array)
        int randomNumber = (int)(Math.random() * initialJsonArray.size());
        System.out.println("This is the random number----> "+randomNumber);

        //Getting the steps jsonarray of a recipe (INSTRUCTIONS and INGREDIENTS nested in STEPS)
        JsonArray theSteps = initialJsonObject.getJsonArray("results")
                                                .getJsonObject(randomNumber) //random number recipe
                                                .getJsonArray("analyzedInstructions") //recipe instructions
                                                .getJsonObject(0) //there is one object in the array
                                                .getJsonArray("steps"); //getting the steps array
    
    //GETTING THE STEPS/INSTRUCTIONS - START
        //getting the length of the steps array AKA the number of steps in the recipe
        Integer numOfSteps = theSteps.size();

        //get the step number and the step instruction and create a jsonobject
        //creating the array with steps numbers and instructions
        JsonArrayBuilder stepsInstructionsBuilder = Json.createArrayBuilder();
        for(int i=0; i<numOfSteps; i++){

            String stepInstruction = theSteps.getJsonObject(i).getString("step");

            stepsInstructionsBuilder.add(stepInstruction);
        }
        JsonArray stepsInstructions = stepsInstructionsBuilder.build();
        System.out.println("the instructions in an array--->"+stepsInstructions.toString());
    //GETTING THE STEPS/INSTRUCTIONS - END

    //GETTING INGREDIENTS - START
        //creating an empty list to store my ingredients
        List<String> ingredientsList = new ArrayList<>();

        //getting the ingredients
        for(int i=0; i<numOfSteps; i++){
            JsonArray singleIngredientsArray = theSteps.getJsonObject(i).getJsonArray("ingredients");

            //size of the array
            Integer singleIngredientArrayLength = singleIngredientsArray.size();
            for(int j=0; j<singleIngredientArrayLength; j++){
                String ingredientName = singleIngredientsArray.getJsonObject(j).getString("name");

                ingredientsList.add(ingredientName);
            }
        }

        List<String> ingredientsListWithoutDups = ingredientsList.stream()
                                                                .distinct()
                                                                .sorted()
                                                                .collect(Collectors.toList());

        //create a jsonarray to store the ingredients
        JsonArrayBuilder ingredientsBuilder = Json.createArrayBuilder();
        for(String i: ingredientsListWithoutDups){
            //System.out.println(i);
            ingredientsBuilder.add(i);
        }
        JsonArray ingredientsArray = ingredientsBuilder.build();
    //GETTING INGREDIENTS - END
    
    //GETTING THE CALORIES - START
        //the shortcut to getting the steps of the first recipe (this includes the INSTRUCTIONS and INGREDIENTS)
        Integer theCalories = initialJsonObject.getJsonArray("results")
        .getJsonObject(randomNumber) //random number recipe
        .getJsonObject("nutrition") //first recipe instructions
        .getJsonArray("nutrients")
        .getJsonObject(0)
        .getInt("amount");
    //GETTING THE CALORIES - END


    //CREATING THE JSONOBJECT THAT SHOULD BE RETURNED - STEPS/INSTRUCTIONS & INGREDIENTS - START
        JsonObject myJsonObject = Json.createObjectBuilder()
        .add("recipe_id", initialJsonArray.getJsonObject(randomNumber).getInt("id"))
        .add("image", initialJsonArray.getJsonObject(randomNumber).getString("image"))
        .add("recipe_name", initialJsonArray.getJsonObject(randomNumber).getString("title"))
        .add("url", initialJsonArray.getJsonObject(randomNumber).getString("spoonacularSourceUrl"))
        .add("calories", theCalories)
        .add("steps", stepsInstructions)
        .add("ingredients", ingredientsArray)
        .build();

        System.out.println("FINAL FINAL RESPONSE --->"+myJsonObject);
        System.out.println(randomNumber); //checking if random number generator works
    //CREATING THE ARRAY THAT SHOULD BE RETURNED - STEPS/INSTRUCTIONS & INGREDIENTS - END

        return myJsonObject;
    }

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("foodfinderapplication0@gmail.com");
        message.setTo("foodfinderapplication0@gmail.com");
        //message.setTo(toEmail); //just change it if want to send to user email
        message.setText(body);
        message.setSubject(subject);

        javaMailSender.send(message);

        System.out.println("Successful: Email has been sent.");
    }


    //generating JWT Token - plain java
    //example:
    //header.payload.signature
    //algotype+tokentype . email+expirationTime . algoHashed(header(base64encoded)+payload(base64encoded),secretkey)
    //https://stackoverflow.com/questions/42966880/java-lang-noclassdeffounderror-javax-xml-bind-datatypeconverter
    public String generateJWT(String email){

    
        return Jwts.builder()
        .setHeaderParam("alg", "HS256")
        .setHeaderParam("typ", "JWT")
        .claim("email", email)
        .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24) ) //24 hour validity in milisecs
        .signWith(SignatureAlgorithm.HS256, "hehehsecret")
        .compact();
    }

    //method to verify jwt token, should be in every controller method
    public ResponseEntity<String> jwtInvalidMessage(){
            JsonObject response = Json.createObjectBuilder().add("error", "invalid token bro").build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response.toString());
        
    }
    
    // https://www.tabnine.com/code/java/methods/io.jsonwebtoken.Jwts/parser
    public boolean verifyJWT(String httpHeader){
        String[] splitHeader = httpHeader.split(" ");
        String token = splitHeader[1];

        //code redundant//just for checking
        String[] parts = token.split("\\.");
        String header = decode(parts[0]);
        String payload = decode(parts[1]);
        String signature = decode(parts[2]);
        System.out.println("need to verify this signature>>>"+signature);
        System.out.println(header+"----"+payload+"----"+signature);
        System.out.println(signature);
        //code redundant//just for checking

        try {
            Jwts.parser().setSigningKey("hehehsecret").parseClaimsJws(token);
            // Jws<Claims> parsedToken = Jwts.parser().setSigningKey("hehehsecret").parseClaimsJws(token);
            //System.out.println(parsedToken);
            System.out.println("Valid token");
            return true;
          } catch (SignatureException e) {
            System.out.println(e.getMessage());
          } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
          } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
          } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
          } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
          }
          return false;
        }

    //decoding each portion of the jwt
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    //verify JWT Backend tests
    public boolean verifyJWTBackend(String token){

        //code redundant//just for checking
        String[] parts = token.split("\\.");
        String header = decode(parts[0]);
        String payload = decode(parts[1]);
        String signature = decode(parts[2]);
        System.out.println("need to verify this signature>>>"+signature);
        System.out.println(header+"----"+payload+"----"+signature);
        System.out.println(signature);
        //code redundant//just for checking

        try {
            Jwts.parser().setSigningKey("hehehsecret").parseClaimsJws(token);
            // Jws<Claims> parsedToken = Jwts.parser().setSigningKey("hehehsecret").parseClaimsJws(token);
            //System.out.println(parsedToken);
            System.out.println("Valid token");
            return true;
          } catch (SignatureException e) {
            System.out.println(e.getMessage());
          } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
          } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
          } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
          } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
          }
          return false;
        }
    
    public String getTeleFavourites(String email){
      return recipeRepo.getTeleFavourites(email);
    }





}
