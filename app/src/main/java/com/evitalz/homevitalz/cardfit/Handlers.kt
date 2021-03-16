package com.evitalz.homevitalz.cardfit

import android.view.View
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.ui.model.MyDeviceModel


interface HandlerHome {
    fun onSpo2Clicked(view : View)
    fun onECGClicked(view : View)
    fun onBGClicked(view : View)
    fun onMealClicked(view: View)
    fun onExcersiceClicked(view: View)
    fun onPillTakenClicked(view: View)
    fun onSleepClicked(view: View)
    fun onDateClicked(view: View)
    fun onPrevClicked(view: View)
    fun onNextClicked(view: View)
    fun onFilterClicked(view: View)
    fun onSortClicked(view: View)
    fun onLiquorClicked(view: View)
}

interface HandlerSpo2{
    fun onSaveClicked(view: View)
    fun oncancelclicked(view: View)
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}

interface HandlerMeal{

    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}

interface HandlerLiquor{
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}


interface HandlerBG{
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
    fun onimage1Clicked(view: View)
    fun onimage2Clicked(view: View)
    fun onimage3Clicked(view: View)
}
interface HandlerExcersice{
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}

interface Handlerpilltaken{
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}

interface HandlerSleep{
    fun onstarttimeClicked(view: View)
    fun onendtimeClicked(view: View)
    fun onDateClicked(view: View)
    fun onTimeClicked(view: View)
}

interface HandlerShowTimeline{
    fun onUpdateClicked(deviceReadings: Device_Readings)
    fun onNoteClicked(deviceReadings: Device_Readings)
}

interface HandlerDeviceConnect{
    fun onAddDeviceClicked(view: View)
    fun onConnectDevice(model: MyDeviceModel)
    fun onConnectClicked(view: View)
}

interface Handlerlogin{
    fun onSignupClicked(view: View)
    fun onLoginClicked(view: View)
}

interface Handlersignup{
    fun onDobClicked(view: View)
    fun onRegisterclicked(view: View)
    fun onBackloginclicked(view: View)
    fun onMaleclicked(view: View)
    fun onFemaleclicked(view: View)
    fun onOtherclicked(view: View)
}

interface HandlerAnalytics{
    fun onPrevious(view: View)
    fun onNext(view: View)
    fun onDateClicked(view: View)
    fun onbgclicked(view: View)
    fun onecgclicked(view: View)
    fun ondayclicked(view: View)
    fun onweekclicked(view: View)
    fun onmonthclicked(view: View)
}

interface HandlerProfile{
    fun onEditClicked(view: View)
}

interface PairHandler{
    fun onPairClicked(model: MyDeviceModel)

}

interface PairDeviceHandler {
    fun onInsertClicked(MyDeviceModel: MyDeviceModel)
    fun onPairClicked(MyDeviceModel: MyDeviceModel)
    fun onDeleteClicked(MyDeviceModel: MyDeviceModel)

}

interface DialogHandler{
    fun onSearchCancelClicked(view: View)
    fun onOkClicked(view: View)
    fun onDataSaveClicked(view: View)

}

interface DialogDevicePlacementHandler{
    fun onLeftHandClicked(view: View)
    fun onLeftWristClicked(view: View)
    fun onLeftLegClicked(view: View)
    fun onChestClicked(view: View)
    fun onTakeTestClicked(view: View)
    fun onTestCancelClicked(view: View)


}

interface DialogRetryHandler{
    fun onRetryClicked(view: View)
    fun onResetClicked(view: View)
    fun onCancelClicked(view: View)

}

interface LoginHandler{
    fun onLoginClicked(view: View)
    fun onForgotPasswordClicked(view: View)
    fun onSignUpClicked(view: View)

}

interface SignupHandler{
    fun onLoginClicked(view: View)
    fun onCreateAccClicked(view: View)
    fun onMaleClicked(view: View)
    fun onFemaleClicked(view: View)
    fun onOtherClicked(view: View)
    fun onVerifyotpClicked(view: View)

}

interface AlarmHandler{
    fun onDismissClicked(view: View)

}

interface Handlerchangepatient {
    fun onPatientnameClicked(view:View)
}


interface HandlerAddnew {
    fun onAddNewClicked(view:View)
    fun onDobClicked(view: View)
    fun onMaleClicked(view: View)
    fun onFemaleClicked(view: View)
    fun onOtherClicked(view: View)
}
interface HandlerQuestions{
    fun onNextClicked(view:View)
}

interface UserInfoHandler{
    fun onEditClicked(view:View)
    fun onbloodgroupclicked(view:View)
    fun onDobClicked(view: View)
    fun onGenderClicked(view: View)
    fun onDiabetictypeClicked(view: View)
    fun onImagePickClicked(view: View)
}

interface HandlerMeasure{
    fun onManualClicked(view:View)
    fun onWirelessClicked(view:View)
}

interface Handlersave{
    fun onSaveClicked(view:View)
    fun onCancelClicked(view:View)
}

interface HandlerRecoverpass{
    fun onChangePassword(view: View)
}







