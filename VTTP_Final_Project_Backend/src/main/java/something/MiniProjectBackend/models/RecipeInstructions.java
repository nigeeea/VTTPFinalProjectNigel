package something.MiniProjectBackend.models;

public class RecipeInstructions {
    
    private Integer recipe_id;
    private String[] steps;
    private String[] ingredients;


    public Integer getRecipe_id() {
        return this.recipe_id;
    }

    public void setRecipe_id(Integer recipe_id) {
        this.recipe_id = recipe_id;
    }


    public String[] getSteps() {
        return this.steps;
    }

    public void setSteps(String[] steps) {
        this.steps = steps;
    }

    public String[] getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

}
