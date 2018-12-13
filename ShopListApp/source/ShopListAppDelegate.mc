using Toybox.WatchUi;
using Toybox.Communications;

class ShopListAppDelegate extends WatchUi.BehaviorDelegate {

    function initialize() {
        BehaviorDelegate.initialize();
    }

    function onMenu() {
    	//action of main view
        //WatchUi.pushView(new Rez.Menus.MainMenu(), new ShopListAppMenuDelegate(), WatchUi.SLIDE_UP);
        
    	System.println("onMenu");
		WatchUi.popView(SLIDE_IMMEDIATE);
        //return true;
    }
    function onBack(){
    	System.println("onBack");
		WatchUi.popView(SLIDE_IMMEDIATE);
    }

}
class ListnerShopListsMenu extends WatchUi.Menu2InputDelegate {
    function initialize() {
        WatchUi.Menu2InputDelegate.initialize();
    }

    function onSelect(item){
    	//save current list
    	if(item.getId() == -1){
    		//todo save
    		System.println("SAVING...");
    		Communications.transmit([shopListsNames,shopListsContent,shopListsStates], null, new CommListener());
    		return;
    	}
    	if(item.getId() == -2){
    		//todo save
    		System.println("REFRESH...");
    		showMainMenu=true;
			WatchUi.popView(SLIDE_IMMEDIATE);
    		return;
    	}
    	
    	currShopList = item.getId();
    	System.println("list selected:"+currShopList);
    	System.println("list size:"+shopListsContent[currShopList].size());
    	
    	
    	
        var checkMenu = new WatchUi.CheckboxMenu({:title=>shopListsNames[currShopList]});
	    for(var i=0;i<shopListsContent[currShopList].size();i++){
        	checkMenu.addItem(new WatchUi.CheckboxMenuItem(shopListsContent[currShopList][i], null, i, shopListsStates[currShopList][i], null));
        }
        WatchUi.pushView(checkMenu, new ListnerListItems(), SLIDE_IMMEDIATE);
    }
    function onDone(){
		WatchUi.popView(SLIDE_IMMEDIATE);
    }
    function onBack(){
		WatchUi.popView(SLIDE_IMMEDIATE);
    }
}
class ListnerListItems extends WatchUi.Menu2InputDelegate {
    function initialize() {
        WatchUi.Menu2InputDelegate.initialize();
    }

    function onSelect(item){
    	shopListsStates[currShopList][item.getId()] = item.isChecked();
    }
}

class CommListener extends Communications.ConnectionListener {
    function initialize() {
        Communications.ConnectionListener.initialize();
    }

    function onComplete() {
        System.println("Transmit Complete");
    }

    function onError() {
        System.println("Transmit Failed");
    }
}