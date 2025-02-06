package edu.uprm.ece.icom4015.jabrisca.test.unit;

import edu.uprm.ece.icom4015.jabrisca.client.ModelStates;
import org.junit.jupiter.api.Test;

public class ModelStatesTest {
    @Test
    public void test_loginSingUp_HappyPathNullValuesNoValueSet(){
        //Prepare
        resetStates();
        ModelStates state = ModelStates.loginsingup;

        //Execute
        String result = state.toString();

        //Test
        assert (result.equals(":username=null,password=null"));
//        assert (state.getStateParameterValue("username")==null);
//        assert (state.getStateParameterValue("password")==null);
    }

    @Test
    public void test_loginSingUp_HappyPathNullValuesOneValueSet(){
        //Prepare
        resetStates();
        ModelStates state = ModelStates.loginsingup;
        String val = "OneUser";
        state.setStateParameterValue("username",val);

        //Execute
        String result = state.toString();

        //Test
        assert (result.equals(":username="+val+",password=null"));
        assert (state.getStateParameterValue("username")==val);
        assert (state.getStateParameterValue("password")==null);
    }
    @Test
    public void test_loginSingUp_HappyPathNullValuesTwoValueSet(){
        //Prepare
        resetStates();
        ModelStates state = ModelStates.loginsingup;
        String val = "OneUser";
        String valPass = "secretPass";
        state.setStateParameterValue("username",val);
        state.setStateParameterValue("password",valPass);
        //Execute
        String result = state.toString();

        //Test
        assert (result.equals(":username="+val+",password="+valPass));
        assert (state.getStateParameterValue("username")==val);
        assert (state.getStateParameterValue("password")==valPass);
    }

    @Test
    public void test_lobby_HappyPathNullValuesNoValueSet(){
        //Prepare
        resetStates();
        ModelStates state = ModelStates.lobby;

        //Execute
        String result = state.toString();

        //Test
        assert (result.equals(":username=null,password=null"));
//        assert (state.getStateParameterValue("username")==null);
//        assert (state.getStateParameterValue("password")==null);
    }

    @Test
    public void test_lobby_HappyPathNullValuesOneValueSet(){
        //Prepare
        resetStates();
        ModelStates state = ModelStates.lobby;
        String val = "OneUser";
        state.setStateParameterValue("username",val);

        //Execute
        String result = state.toString();

        //Test
        assert (result.equals(":username="+val+",password=null"));
        assert (state.getStateParameterValue("username")==val);
        assert (state.getStateParameterValue("password")==null);
    }
    private static void resetStates() {
        for(ModelStates state: ModelStates.values()){
            state.setStateParameterValues(new Object[]{});
        }
    }
}
