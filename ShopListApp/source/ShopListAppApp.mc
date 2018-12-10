using Toybox.Application;
using Toybox.WatchUi;

var currShopList     =0;
var shopListsNames   =[];
var shopListsContent =[];
var shopListsStates  =[];
/*
var shopListsNames=["Ménage","Boisson"];
var shopListsContent=[["Linge","Éponge"],["Café","Lait","Jus pomme"]];
var shopListsStates=[[false,false],[false,false,false]];
*/

class ShopListAppApp extends Application.AppBase {

    function initialize() {
        AppBase.initialize();
        if(Communications has :registerForPhoneAppMessages) {
            Communications.registerForPhoneAppMessages(method(:onPhone));
        }
    }

    // onStart() is called on application start up
    function onStart(state) {
    	//get settings
    	if(Storage.getValue("shopListsNames") != null){
    		shopListsNames = Storage.getValue("shopListsNames");
    	}
    	if(Storage.getValue("shopListsContent") != null){
    		shopListsContent = Storage.getValue("shopListsContent");
    	}
    	if(Storage.getValue("shopListsStates") != null){
    		shopListsStates = Storage.getValue("shopListsStates");
    	}
    	
    	//Test
    	System.println([shopListsNames,shopListsContent,shopListsStates]);
    }

    // onStop() is called when your application is exiting
    function onStop(state) {
    	//save values
    	Storage.setValue("shopListsNames",shopListsNames);
    	Storage.setValue("shopListsContent",shopListsContent);
    	Storage.setValue("shopListsStates",shopListsStates);
    	
    	//unregister listener
        //Communications.registerForPhoneAppMessages(null);
    }

    // Return the initial view of your application here
    function getInitialView() {
        return [ new ShopListAppView(), new ShopListAppDelegate() ];
    }
    
    function onPhone(msg) {
    	shopListsNames   = msg.data[0];
    	shopListsContent = msg.data[1];
    	shopListsStates  = msg.data[2];
        WatchUi.requestUpdate();
    }

}
