package something.MiniProjectBackend.repositories;

public class Queries {
    
    //REGISTER USER QUERY
    public static final String SQL_REGISTER_USER = "insert into users (email, name, password) values (?,?,?)";

    //get method test
    public static final String SQL_SELECT_BY_NAME = "select id, firstName, lastName from customers where firstName = ?";

    //post method user
    public static final String SQL_SAVE_RECIPE = "insert into recipes (recipe_id, recipe_name, image, url, calories, email, cuisine) values (?,?,?,?,?,?,?)";

    //post user in to user table
    public static final String SQL_INSERT_USER = "insert into users (email, password) values (?,?)";
    //post user contact details into contact table
    public static final String SQL_INSERT_CONTACT_DETAILS = "insert into contact (email, full_name, contact_number, address, postal_code) values (?,?,?,?,?)";

    //get the user info for use authentication on login page
    public static final String SQL_GET_USER_LOGIN ="select email, password from users where email = ?";

    //get favourites
    public static final String SQL_GET_FAVOURITES = "select recipe_id, recipe_name, image, url, calories,cuisine from recipes where email = ?";

    //get user profile
    public static final String SQL_GET_USER_PROFILE = "select email, full_name, contact_number, address, postal_code from contact where email = ?";

    //updating user profile in contact without email change
    public static final String SQL_UPDATE_USER_PROFILE_IN_CONTACT = "update contact set full_name = ?, contact_number= ?, address = ?, postal_code = ? where email = ?";

    //updating user profile in user table with new email
    public static final String SQL_UPDATE_USER_EMAIL_IN_USERS = "update users set email = ? where email = ?";

    //updating user profile in contact with email change
    public static final String SQL_UPDATE_USER_PROFILE_IN_CONTACT_W_EMAIL="update contact set email = ?, full_name = ?, contact_number= ?, address = ?, postal_code = ? where email = ?";

    //updating old email with new email in recipes table
    public static final String SQL_UPDATE_USER_EMAIL_IN_RECIPES ="update recipes set email = ? where email = ?";

    //disable foreign key checks
    public static final String SQL_DISABLE_FOREIGN_KEY_CHECKS ="set foreign_key_checks=0";

    //enable foreign key checks
    public static final String SQL_ENABLE_FOREIGN_KEY_CHECKS ="set foreign_key_checks=1";

    //delete a recipe from favourites
    public static final String SQL_DELETE_RECIPE_FROM_FAV = "delete from recipes where email = ? and recipe_id = ?";
}
