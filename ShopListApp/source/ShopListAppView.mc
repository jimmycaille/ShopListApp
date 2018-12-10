using Toybox.WatchUi;

class ShopListAppView extends WatchUi.View {
	var firstView;
    function initialize() {
    	firstView = true;
        View.initialize();
    }

    // Load your resources here
    function onLayout(dc) {
        setLayout(Rez.Layouts.MainLayout(dc));
    }

    // Called when this View is brought to the foreground. Restore
    // the state of this View and prepare it to be shown. This includes
    // loading resources into memory.
    function onShow() {
    	//trick to show directly menu https://forums.garmin.com/forum/developers/connect-iq/141922-starting-an-app-in-a-menu
    	if(firstView){
    		firstView = false;
            var customMenu = new WatchUi.Menu2({:title=>"Shop Lists"});
            
	        for(var i=0;i<shopListsNames.size();i++){
	            customMenu.addItem(new WatchUi.MenuItem(shopListsNames[i], null, i, null));
            }
	        customMenu.addItem(new WatchUi.MenuItem("* SAVE ALL *", null, -1, null));
	        
	        WatchUi.pushView(customMenu, new ListnerShopListsMenu(), SLIDE_IMMEDIATE);
        }else{
        	WatchUi.popView(WatchUi.SLIDE_IMMEDIATE);
        }
    }

    // Update the view
    function onUpdate(dc) {
        // Call the parent onUpdate function to redraw the layout
        View.onUpdate(dc);
    }

    // Called when this View is removed from the screen. Save the
    // state of this View here. This includes freeing resources from
    // memory.
    function onHide() {
    }

}
