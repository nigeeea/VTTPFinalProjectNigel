package something.MiniProjectBackend.models;

import jakarta.json.JsonObject;

public class Recipe {
    
    //instance variables
    private Integer recipe_id;

    private String recipe_name;

    //private String maxCalories;

    private String image;

    private String url;

    private Integer calories;

    //getter and setters


    public Integer getRecipe_id() {
        return this.recipe_id;
    }

    public void setRecipe_id(Integer recipe_id) {
        this.recipe_id = recipe_id;
    }
    
    public String getRecipe_name() {
        return this.recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }
    

    // public String getMaxCalories() {
    //     return this.maxCalories;
    // }

    // public void setMaxCalories(String maxCalories) {
    //     this.maxCalories = maxCalories;
    // }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCalories() {
        return this.calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    //method to convert JSONObject to food object with the set instance variables
    public Recipe fromJSONToFood(JsonObject foodJSON){
        Recipe myFood = new Recipe();
        myFood.setRecipe_name(foodJSON.getString("recipeName"));
        myFood.setImage(foodJSON.getString("image"));
        myFood.setRecipe_id(foodJSON.getInt("id"));
        myFood.setUrl(foodJSON.getString("url"));
        myFood.setCalories(foodJSON.getInt("calories"));
        return myFood;
    }
}
