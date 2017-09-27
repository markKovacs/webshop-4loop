
import com.codecool.shop.order.InputField;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValid(String inputValue, InputField inputField) {
        Pattern compiledPattern = Pattern.compile(inputField.getRegex());
        if (!compiledPattern.matcher(inputValue).matches()) {
            return false;
        }
        return true;
    }


}