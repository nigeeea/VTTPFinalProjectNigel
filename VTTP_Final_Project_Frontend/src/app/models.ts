export interface AuthenticationResult{
    authenticated: boolean;
    token: string;
}

export interface RegisterUserResult{
    registered: boolean;
    token: string;
}

export interface Recipe{
    recipe_id: number;
    recipe_name: string;
    image: string;
    url: string;
    calories: number;
    cuisine: string;
    steps: string[];
    ingredients: string[];
}

export interface SaveRecipeResult{
    saved: boolean;
}

export interface UserProfile{
    email: string;
    full_name: string;
    contact_number: number;
    address: string;
    postal_code: number;
}

export interface UserProfileUpdateResult{
    profile_updated: boolean;
}

export interface DeleteRecipeResult{
    recipe_deleted: boolean;
}

export interface RecipeInstructions{
    steps: string[];
    ingredients: string[];
}