package something.MiniProjectBackend.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.google.common.hash.Hashing;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import something.MiniProjectBackend.models.RecipeInstructions;
import something.MiniProjectBackend.models.User;
import something.MiniProjectBackend.models.UserProfile;

import static something.MiniProjectBackend.repositories.Queries.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class RecipeRepository {
    
    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired MongoTemplate mongoTemplate;


    //METHOD TO REGISTER USER IN SQL - METHOD 1
    public boolean registerUserSql(String email, 
                                    String password, 
                                    String full_name, 
                                    String contact_number, 
                                    String address, 
                                    String postal_code){

        password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();

        try {
            //create the query to insert into the users table
            Integer rowaAffected = jdbcTemplate.update(SQL_INSERT_USER, email, password);
            if(rowaAffected != 1){return false;}
        } catch (Exception e) {
            //if fail handle exception
            //print error
            System.out.println("users table insert failed>>> "+ e);
            return false;
        }

        try {
            //create the query to insert into the contact table
            Integer rowsAffected = jdbcTemplate.update(SQL_INSERT_CONTACT_DETAILS, 
                                                    email, 
                                                    full_name, 
                                                    Integer.parseInt(contact_number), 
                                                    address, 
                                                    Integer.parseInt(postal_code));
            if(rowsAffected != 1){return false;}
            else{return true;}
        } catch (Exception e) {
            //if fail handle exception
            //print error
            System.out.println("contact table insert failed>>> "+ e);
            return false;
        }
    }

    //METHOD TO AUTHENTICATE USER/LOGIN - METHOD 2
    public boolean authenticateUser(User user){

        String email = user.getEmail();
        String password = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();

        try {
            SqlRowSet rs = null;

            System.out.println(email);
            System.out.println(password);
            rs = jdbcTemplate.queryForRowSet(SQL_GET_USER_LOGIN, email);

                if(rs.next()){
                    String returnedPassword = rs.getString("password");
                    System.out.println(returnedPassword);
                        
                        if(password.equals(returnedPassword))
                        {return true;}
                        
                        else{
                            System.out.println("failed to login wrong password LOL");
                            return false;}
        
                }
                else{
                    System.out.println("email wrong or user does not exist");
                    return false;
                }
        } catch (Exception e) {
            //if query fails, print error and return false
            System.out.println("error when querying for user Auth>>>"+e.getMessage());
            return false;
        }
    } 

    //METHOD TO SAVE RECIPE - UPGRADED - METHOD 3
    public boolean SaveRecipeTwo(RecipeInstructions recipeInstructions,
                                Integer recipe_id,
                                String recipe_name,
                                String image,
                                String url,
                                Integer calories,
                                String email,
                                String cuisine ){

        try {
                    try {
                        //convert the recipe instructions into a json object
                    recipeInstructions.setRecipe_id(recipe_id);
                    mongoTemplate.insert(recipeInstructions, "recipe_instructions");
                    
                        } catch (Exception e) {
                        System.out.println("error "+ e.getMessage());
                        }
            
                    Integer rowsAffected = jdbcTemplate.update(SQL_SAVE_RECIPE, 
                                                        recipe_id,
                                                        recipe_name,
                                                        image,
                                                        url,
                                                        calories,
                                                        email,
                                                        cuisine);
                    if(rowsAffected==1){ 
                        System.out.println("saved recipe "+recipe_id+" into "+email);
                        return true;}
                    else{return false;}
            } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
            }
                                }

    //METHOD TO RETRIEVE ALL FAVOURITE RECIPES - METHOD 4
    public JsonArray getFavourites(String email){

        SqlRowSet rs = null;
        rs = jdbcTemplate.queryForRowSet(SQL_GET_FAVOURITES,email);
        JsonArrayBuilder favouritesBuilder = Json.createArrayBuilder();

        while(rs.next()){
            JsonObject singleFav = Json.createObjectBuilder()
                                        .add("recipe_id", rs.getInt("recipe_id"))
                                        .add("recipe_name", rs.getString("recipe_name"))
                                        .add("image", rs.getString("image"))
                                        .add("url", rs.getString("url"))
                                        .add("calories", rs.getInt("calories"))
                                        .add("cuisine", rs.getString("cuisine"))
                                        .build();

                favouritesBuilder.add(singleFav);
        }
        JsonArray favourites = favouritesBuilder.build();
        return favourites;
    }

    //TELE METHOD TO RETRIEVE ALL FAVOURITE RECIPES
    public String getTeleFavourites(String email){
        SqlRowSet rs = null;
        rs = jdbcTemplate.queryForRowSet(SQL_GET_FAVOURITES,email);
        JsonArrayBuilder favouritesBuilder = Json.createArrayBuilder();

        String response = "";
        
        while(rs.next()){
            // Integer count = 1;
            String recipeName = rs.getString("recipe_name")+"\n";
            response = response+recipeName;
            // count+=1;
        }

        return response;
    }


    //METHOD TO GET USER PROFILE - METHOD 5
    public JsonObject getUserProfile(String email){

        try {
            SqlRowSet rs = null;
            rs = jdbcTemplate.queryForRowSet(SQL_GET_USER_PROFILE, email);
            
            JsonObjectBuilder profileBuilder = Json.createObjectBuilder();

                while(rs.next()){
                    profileBuilder.add("email", rs.getString("email"))
                                .add("full_name",rs.getString("full_name"))
                                .add("contact_number", rs.getInt("contact_number"))
                                .add("address",rs.getString("address"))
                                .add("postal_code", rs.getInt("postal_code"));
                }
            JsonObject response = profileBuilder.build();

            return response;

        } catch (Exception e) {
            JsonObject response = Json.createObjectBuilder().add("error", e.getMessage()).build();
            return response;
        }
        
    }

    //METHOD TO UPDATE USER PROFILE - METHOD 6 - JUST CONTACT TABLE
    //FIRST - we need to check if the email has been edited - this can be done in front end
    //if edited send something to backend - since email (unique parent value) has not been edited
    //we do not need to jdbcTemplate.execute("SET foreign_key_checks = 0") and can just update the contacts table freely
    //however if email is edited, set fk checks=0 edit parent table
    //edit the contact table
    //edit the recipes table and set the fk check =1
    public boolean updateUserProfile(UserProfile userProfile){

        //get the values of the userprofile instance variables
        String email = userProfile.getEmail();
        String full_name = userProfile.getFull_name();
        Integer contact_number = userProfile.getContact_number();
        String address = userProfile.getAddress();
        Integer postal_code = userProfile.getPostal_code();
        
        //update the user profile
        try {
            Integer rowsAffected = jdbcTemplate.update(SQL_UPDATE_USER_PROFILE_IN_CONTACT, 
                                                                full_name,
                                                                contact_number,
                                                                address,
                                                                postal_code,
                                                                email);
            if(rowsAffected ==1){
                return true;
            }
            else{return false;}
        
        } catch (Exception e) {
            System.out.println("error"+e.getMessage());
            return false;
        }
        
    }

    //METHOD TO UPDATE USER PROFILE - METHOD 6 - UPDATE ALL TABLES
    public boolean updateEmailAndUserProfile(UserProfile userProfile, String oldEmail){

        String address =  userProfile.getAddress();
        Integer contact_number = userProfile.getContact_number();
        String email = userProfile.getEmail();
        String full_name = userProfile.getFull_name();
        Integer postal_code = userProfile.getPostal_code();

        //set foreign key checks = 0;
        //update the users table
        //update the contact table (1-1)
        //update the recipe tables (1-many)
        //set foreign key checks = 1;

        //insert try catch
        jdbcTemplate.execute(SQL_DISABLE_FOREIGN_KEY_CHECKS);
        Integer usersRowsAffected = jdbcTemplate.update(SQL_UPDATE_USER_EMAIL_IN_USERS, email, oldEmail);
        Integer contactRowsAffected = jdbcTemplate.update(SQL_UPDATE_USER_PROFILE_IN_CONTACT_W_EMAIL, email, full_name, contact_number, address, postal_code, oldEmail);
        jdbcTemplate.update(SQL_UPDATE_USER_EMAIL_IN_RECIPES, email, oldEmail);
        jdbcTemplate.execute(SQL_ENABLE_FOREIGN_KEY_CHECKS);

        if(usersRowsAffected == 1 && contactRowsAffected == 1){
            return true;
        }

        else{
            return false;
        }
    }

    //METHOD TO DELETE RECIPE FROM FAVOURITES - METHOD 7
    public boolean deleteRecipe(String email, Integer recipe_id){

        try {
            Integer rowsAffected = jdbcTemplate.update(SQL_DELETE_RECIPE_FROM_FAV, email, recipe_id);
            if(rowsAffected == 1){
                System.out.println("deleted recipe "+recipe_id+" from "+email);
                return true;
            }
            else{return false;}
        } catch (Exception e) {
            System.out.println("error: "+e.getMessage());
            return false;
        }
        
    }

    //METHOD TO GET RECIPE INSTRUCTIONS FROM MONGO - METHOD 8
    public JsonObject getSingleRecipeInstructions(String recipe_id){

        Query query = new Query();
        Integer recipe_id_int = Integer.parseInt(recipe_id);
        query.addCriteria(Criteria.where("recipe_id").is(recipe_id_int));
        Document d = mongoTemplate.findOne(query, Document.class, "recipe_instructions");


        List<String> steps = d.getList("steps", String.class);
        List<String> ingredients = d.getList("ingredients", String.class);

        for(String s: steps){System.out.println(s);}
        for(String s: ingredients){System.out.println(s);}

        //create the jsonObject to return
        JsonArrayBuilder stepsBuilder = Json.createArrayBuilder();
        for(String s: steps){stepsBuilder.add(s);}
        JsonArray stepsArray = stepsBuilder.build();

        JsonArrayBuilder ingredBuilder = Json.createArrayBuilder();
        for(String s: ingredients){ingredBuilder.add(s);}
        JsonArray ingredArray = ingredBuilder.build();

        JsonObject response = Json.createObjectBuilder().add("steps", stepsArray)
                                                        .add("ingredients", ingredArray)
                                                        .build();

        System.out.println(response);
        return response;
    }
}


