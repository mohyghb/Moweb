package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill;

import android.content.Context;

import androidx.annotation.IntDef;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.HashMap;
import java.util.Objects;

// coming soon moved to another time to add this feature
public class MoWebAutoFill implements MoSavable, MoLoadable {

    public static final int OFF = 0;

    public static final int NAME = 1;
    public static final int HONORIFIC_PREFIX = 2;
    public static final int GIVEN_NAME = 3;
    public static final int ADDITIONAL_NAME = 4;
    public static final int FAMILY_NAME = 5;
    public static final int HONORIFIC_SUFFIX = 6;
    public static final int NICKNAME = 7;


    public static final int USERNAME = 8;
    public static final int EMAIL = 9;
    public static final int NEW_PASSWORD = 10;
    public static final int CURRENT_PASSWORD = 11;
    public static final int ONE_TIME_CODE = 12;
    public static final int ORGANIZATION_TITLE = 13;
    public static final int ORGANIZATION = 14;

    public static final int STREET_ADDRESS = 15;
    public static final int ADDRESS_LINE_1 = 16;
    public static final int ADDRESS_LINE_2 = 17;
    public static final int ADDRESS_LINE_3 = 18;
    public static final int ADDRESS_LEVEL_4 = 19;
    public static final int ADDRESS_LEVEL_3 = 20;
    public static final int ADDRESS_LEVEL_2 = 21;
    public static final int ADDRESS_LEVEL_1 = 22;

    public static final int COUNTRY = 23;
    public static final int COUNTRY_NAME = 24;
    public static final int POSTAL_CODE = 25;

    public static final int CC_NAME = 26;
    public static final int CC_GIVEN_NAME = 27;
    public static final int CC_ADDITIONAL_NAME = 28;
    public static final int CC_FAMILY_NAME = 29;
    public static final int CC_NUMBER = 30;
    public static final int CC_EXP = 31;
    public static final int CC_EXP_MONTH = 32;
    public static final int CC_EXP_YEAR = 33;
    public static final int CC_CSC = 34;
    public static final int CC_TYPE = 35;

    public static final int TRANSACTION_CURRENCY = 36;
    public static final int TRANSACTION_AMOUNT = 37;
    public static final int LANGUAGE = 38;

    public static final int B_DAY = 39;
    public static final int B_DAY_DAY = 40;
    public static final int B_DAY_MONTH = 41;
    public static final int B_DAY_YEAR = 42;

    public static final int SEX = 43;
    public static final int TEL = 44;
    public static final int TEL_COUNTRY_CODE = 45;
    public static final int TEL_NATIONAL = 46;
    public static final int TEL_AREA_CODE = 47;
    public static final int TEL_LOCAL = 48;
    public static final int TEL_EXTENSION = 49;
    public static final int IMPP = 50;

    public static final int URL = 51;
    public static final int PHOTO = 52;
    public static final String PASSWORD_KEY = "password";
    public static final String USERNAME_KEY = "username";


    public static HashMap<String, Integer> autoCompleteTypes = new HashMap<String, Integer>() {{
        put("off", OFF);
        put("name", NAME);
        put("honorific-prefix", HONORIFIC_PREFIX);
        put("given-name", GIVEN_NAME);
        put("additional-name", ADDITIONAL_NAME);
        put("family-name", FAMILY_NAME);
        put("honorific-suffix", HONORIFIC_SUFFIX);
        put("nickname", NICKNAME);
        put("username", USERNAME);
        put("email", EMAIL);
        put("new-password", NEW_PASSWORD);
        put("current-password", CURRENT_PASSWORD);
        put("one-time-code", ONE_TIME_CODE);
        put("organization-title", ORGANIZATION_TITLE);
        put("organization", ORGANIZATION);
        put("street-address", STREET_ADDRESS);
        put("address-line1", ADDRESS_LINE_1);
        put("address-line2", ADDRESS_LINE_2);
        put("address-line3", ADDRESS_LINE_3);
        put("address-level4", ADDRESS_LEVEL_4);
        put("address-level3", ADDRESS_LEVEL_3);
        put("address-level2", ADDRESS_LEVEL_2);
        put("address-level1", ADDRESS_LEVEL_1);
        put("country", COUNTRY);
        put("country-name", COUNTRY_NAME);
        put("postal-code", POSTAL_CODE);
        put("cc-name", CC_NAME);
        put("cc-given-name", CC_GIVEN_NAME);
        put("cc-additional-name", CC_ADDITIONAL_NAME);
        put("cc-family-name", CC_FAMILY_NAME);
        put("cc-number", CC_NUMBER);
        put("cc-exp", CC_EXP);
        put("cc-exp-month", CC_EXP_MONTH);
        put("cc-exp-year", CC_EXP_YEAR);
        put("cc-csc", CC_CSC);
        put("cc-type", CC_TYPE);
        put("transaction-currency", TRANSACTION_CURRENCY);
        put("transaction-amount", TRANSACTION_AMOUNT);
        put("language", LANGUAGE);
        put("bday", B_DAY);
        put("bday-day", B_DAY_DAY);
        put("bday-month", B_DAY_MONTH);
        put("bday-year", B_DAY_YEAR);
        put("sex", SEX);
        put("tel", TEL);
        put("tel-country-code", TEL_COUNTRY_CODE);
        put("tel-national", TEL_NATIONAL);
        put("tel-area-code", TEL_AREA_CODE);
        put("tel-local", TEL_LOCAL);
        put("tel-extension", TEL_EXTENSION);
        put("impp", IMPP);
        put("url", URL);
        put("photo", PHOTO);
    }};


    public static final HashMap<String, Integer> fieldTypes = new HashMap<String, Integer>() {{
        put("email", TYPE_EMAIL);
        put("password", TYPE_PASSWORD);
        put("text", TYPE_TEXT);
    }};


    @IntDef(value = {TYPE_EMAIL, TYPE_PASSWORD, TYPE_TEXT, TYPE_OTHER})
    public @interface FieldType {
    }

    public static final int TYPE_EMAIL = 0;
    public static final int TYPE_PASSWORD = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_OTHER = 3;

    private String id;
    private String value;
    @FieldType
    private int fieldType;
    private int autoCompleteType;


    public MoWebAutoFill() {
    }

    public MoWebAutoFill(String id, String value, int fieldType, int autoCompleteType) {
        this.setId(id);
        this.setValue(value);
        this.fieldType = fieldType;
        this.autoCompleteType = autoCompleteType;
    }

    public String getId() {
        return id;
    }

    public MoWebAutoFill setId(String id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MoWebAutoFill setValue(String value) {
        this.value = value;
        return this;
    }

    public int getAutoCompleteType() {
        return autoCompleteType;
    }

    public int getFieldType() {
        return fieldType;
    }

    public boolean isPassword() {
        return this.fieldType == TYPE_PASSWORD ||
                this.autoCompleteType == CURRENT_PASSWORD ||
                this.autoCompleteType == NEW_PASSWORD;
    }

    public boolean isEmail() {
        return this.fieldType == TYPE_EMAIL || this.autoCompleteType == EMAIL;
    }

    public boolean isText() {
        return this.fieldType == TYPE_TEXT;
    }

    public boolean isUsername() {
        return this.autoCompleteType == USERNAME;
    }

    public boolean isNotUserPass() {
        return !this.isUsername() && !this.isPassword();
    }

    public boolean isOff() {
        return this.autoCompleteType == OFF;
    }

    public boolean isNotOff() {
        return !this.isOff();
    }

    /**
     * sets the type based on the type
     * that is passed from the input
     * field inside js
     * we convert it into an integer
     * for later use if the type is
     * applicable or give it TYPE_OTHER
     * if none of the other types make sense
     * for this type
     *
     * @param fieldType of the auto fill
     * @return this for nested calling
     */
    public MoWebAutoFill setFieldType(String fieldType) {
        Integer potential = fieldTypes.get(fieldType);
        if (potential != null) {
            this.fieldType = potential;
        } else {
            this.fieldType = TYPE_OTHER;
        }
        return this;
    }

    /**
     * sets the auto complete
     * type of this class
     *
     * @param a
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    public MoWebAutoFill setAutoCompleteType(String a) {
        if (autoCompleteTypes.containsKey(a)) {
            // then we have that type
            this.autoCompleteType = autoCompleteTypes.get(a);
        } else {
            this.autoCompleteType = OFF;
        }
        return this;
    }


    public String getAutocomplete() {
        for (String s : autoCompleteTypes.keySet()) {
            if (autoCompleteTypes.get(s) == this.autoCompleteType) {
                return s;
            }
        }
        return "";
    }

    @Override
    public void load(String s, Context context) {
        String[] l = MoFile.loadable(s);
        this.id = l[0];
        this.value = l[1];
        this.fieldType = Integer.parseInt(l[2]);
        this.autoCompleteType = Integer.parseInt(l[3]);
    }

    @Override
    public String getData() {
        return MoFile.getData(id, value, fieldType, autoCompleteType);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoWebAutoFill autoFill = (MoWebAutoFill) o;
        return fieldType == autoFill.fieldType &&
                autoCompleteType == autoFill.autoCompleteType &&
                Objects.equals(id, autoFill.id) &&
                Objects.equals(value, autoFill.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, fieldType, autoCompleteType);
    }


    /**
     * @param autocomplete from input field inside js
     * @return true if the auto fill is turned off
     * for the field
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean autoFillIsOff(String autocomplete) {
        if (autocomplete == null || autocomplete.isEmpty()) {
            // if the auto complete is null or empty or off
            return true;
        }
        Integer val = autoCompleteTypes.get(autocomplete);
        return val == null || val == OFF;
    }

    /**
     * if the auto complete is a current password
     * or username then this is a user pass auto fill
     *
     * @param autocomplete
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean isUserPassAutoFill(String id, String type, String autocomplete) {
        if (id != null && (id.toLowerCase().contains(USERNAME_KEY)
                || id.toLowerCase().contains(PASSWORD_KEY))) {
            return true;
        }
        if (type != null && fieldTypes.containsKey(type) && fieldTypes.get(type) == TYPE_PASSWORD) {
            return true;
        }
        Integer val = autoCompleteTypes.get(autocomplete);
        return val != null && val != OFF && (val == CURRENT_PASSWORD || val == USERNAME || val == EMAIL);
    }


    /**
     * anything that is not a user password
     * and not add credit card as another type of auto fill
     * then it is a general auto complete
     *
     * @param autocomplete
     * @return true if it is a general auto fill
     */
    public static boolean isGeneralAutoFill(String id, String type, String autocomplete) {
        return !isUserPassAutoFill(id, type, autocomplete) && !isCreditCardAutoFill(autocomplete);
    }

    /**
     * if auto fill that is passed in is
     * related to credit card auto fill
     *
     * @param autocomplete from field inside js
     * @return true if req are met
     */
    public static boolean isCreditCardAutoFill(String autocomplete) {
        // todo stub
        return false;
    }


}
