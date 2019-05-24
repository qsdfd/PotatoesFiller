import org.osbot.rs07.api.Bank.BankMode;
import org.osbot.rs07.api.filter.AreaFilter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.RandomBehaviourHook;
import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@ScriptManifest(name = "Potato Sack Filler", author = "dokato", version = 1.0, info = "", logo = "") 
public class MainPotato extends Script {
	
	private static final Color standardTxtColor =new Color(255, 255, 255);
	private static final Color breakRectColor = new Color(0, 0, 0, 175);
	
	private static final Rectangle breakRect = new Rectangle(190, 110, 390, 50);
	
	private boolean startb = true;

	private long timeBegan;
	private long timeRan;
	private long timeReset;
	private long timeSinceReset;
	private long timeBotted;
	private long timeOffline;
	
	private String status;
	
	private long timeLastBreaked;
	private long timeSinceLastBreaked;
	private long timeBreakStart;
	
	private static final long milisecondsPerMinute = 60000; 
	private long bottingTime = 52 * milisecondsPerMinute;
	private long breakingTime = 14 * milisecondsPerMinute;
	private static final long randomizeValue = 5 * milisecondsPerMinute;
	
	private long timeBotting;
	private long timeBreaing;
	
	private boolean resetBreakCheck = true;
	private boolean hasStarted = false;
	
	//-----------------------------------------------------------------------------
	
	private static final int potatoesSackId = 5438;
	private static final int potatoesSackNotedId = 5439;
	
	private static final int emptySackId = 5418;
	private static final int potatoId = 1942;
	private static final int emptySackNotedId = 5419;
	private static final int potatoNotedId = 1943;
	
	private static final int bankChestId = 19051;
	
	private int sacksInBank;
	private int trades;
	private int sackstraded;
	
	private boolean hasToTrade;
	
	private String supplier;
	
	private boolean sendTradeRequest;
	
	@Override
    public void onStart(){
		resetTime();
		mouseListenerStuff();
		randomEventStuff();
		
		this.trades = 0;
		this.sackstraded = 0;
		this.hasToTrade = false;
		this.supplier = Filer.getSupplier();
    }
	
    public int onLoop() throws InterruptedException{
    	status="loop started";
    	if(hasToTrade){
	    	if(!needToBreak()){
		    	if(getClient().isLoggedIn()){
		    		breakTimeProcedures();
		    		if(isInvGood()){
			    		fillSack();
		    		}else{
		    			bank();
		    		}
		    	}
	    	}else{
				doBreak();
			}
    	}else{
    		if(!isDoneTrading()){
    			status="checking file";
	    		if(Filer.getCheck()){
	    			status="getting acc";
	    			if(Filer.getAcc().equals(myPlayer().getName())){
	    				status="checking if i've got noted sack";
	    				if(getInventory().contains(potatoesSackNotedId)){
	    					if(getBank().isOpen()){
	    						status="closing bank";
	    						getBank().close();
	    					}else{
	    						traderProcedures();
	    					}
	    				}else{
	    					bank();
	    				}
	    			}else{
	    				wait();
	    			}
	    		}else{
	    			Filer.setAcc(myPlayer().getName());
	    			Filer.setWorld(getWorlds().getCurrentWorld());
	    			Filer.setCheck(true);
	    		}
    		}else{
    			status="just done trading";
    			
    			trades++;
    			sackstraded+=sacksInBank;
    			
    			hasToTrade = false;
    			
    			status="cleaning files";
    			Filer.cleanFiles();
    		}
    	}
    	return 0;
    }

    @Override
    public void onPaint(Graphics2D g1){
    	timeStuff();
		
		Graphics2D g = g1;

		int startY = 150;
		int increment = 15;
		int value = (-increment);
		int x = 20;
		
		g.setFont(new Font("Arial", 0, 13));
		g.setColor(standardTxtColor);
		//g.drawString("Acc: " + getBot().getUsername().substring(0, getBot().getUsername().indexOf('@')), x,getY(startY, value+=increment));
		g.drawString("World: " + getWorlds().getCurrentWorld(),x,getY(startY, value+=increment));
		value+=increment;
		g.drawString("Version: " + getVersion(), x, getY(startY, value+=increment));
		g.drawString("Runtime: " + ft(this.timeRan), x, getY(startY, value+=increment));
		g.drawString("Time botted: " + ft(this.timeBotted), x, getY(startY, value+=increment));
		if(hasStarted)
			g.drawString("Last break: " + ft(this.timeSinceLastBreaked), x, getY(startY, value+=increment));
		g.drawString("Status: " + status, x, getY(startY, value+=increment));
		value+=increment;
		g.drawString("Sacks in bank: " + this.sacksInBank, x, getY(startY, value+=increment));
		
		if(hasStarted && needToBreak()){
			g.setColor(breakRectColor);
			fillRect(g, breakRect);
			g.setColor(standardTxtColor);
			g.drawString("Have to break for: " + ft(this.timeBreaing) , 275, 130);
			g.drawString("Have been breaking for: " + ft((System.currentTimeMillis() - this.timeBreakStart)), 275, 145);
		}
    }
    
    public void onMessage(Message message) throws InterruptedException {
    	String txt = message.getMessage().toLowerCase().trim();
		
		if(txt.contains("Sending trade offer"))
			this.sendTradeRequest = false;
	}

	public void onExit() {
		
	}
	
	private boolean isDoneTrading(){
		return getInventory().contains(potatoNotedId) && getInventory().contains(emptySackNotedId);
	}
	
	private void traderProcedures() throws InterruptedException{
		status="trader procedures";
		if(getTrade().isCurrentlyTrading()){
			status = "currently trading";
			if(getTrade().isFirstInterfaceOpen()){
				status = "first trade window ";
				if(getInventory().isEmpty()){
					status = "Accepting..";
    				getTrade().acceptTrade();
				}else{
					status = "putting items in trade";
					getInventory().getItem(potatoesSackNotedId).interact("Offer-All");
					sleep(random(250, 780));
				}
			}else if(getTrade().isSecondInterfaceOpen()){
    			status = "second trade window";
    			getTrade().acceptTrade();
    		}
		}else{
			status="check if i need to send trade request";
			if(sendTradeRequest){
				status="about to get the muler";
				Player sup = getPlayer(this.supplier,7);
				status="checking if muler != null";
				if(sup != null){
					status="about to interact with muler";
					sup.interact("Trade with");
					sleep(random(3500,5500));
				}
			}
		}
	}
	
	private Player getPlayer(String name, int radius){
    	status = "getting the muler";
    	Player aPlayer = null;
    	status = "getting players List";
    	List<Player> playerList = getPlayers().filter(new AreaFilter<Player>(myPlayer().getArea(radius)));
    	status = "about to iterate over players list";
    	for(Player player : playerList){
			status = "iteraying over players";
			if(player.getName().equals(name)){
				aPlayer=player;
				break;
			}
		}
		return aPlayer;
    }
	
	private boolean isInvGood(){
		status="returning if inv contains stuff";
		return getInventory().contains(emptySackId) && getAmountInInv(potatoId) >= 10;
	}
	
	private void fillSack() throws InterruptedException{
		status="checking is bank is open 1";
		if(getBank().isOpen() && getInventory().isFull()){
			status="closing bank";
			getBank().close();
		}else{
			status="gonna fille the sack";
			getInventory().getItem(emptySackId).interact("Fill");
			sleep(random(450,700));
		}
	}
	
	private void bank() throws InterruptedException{
		status="checking if bank is open 2";
		if(getBank().isOpen()){
			if(!hasToTrade){
				this.sacksInBank = getBank().getItem(potatoesSackId).getAmount();
				if(getInventory().isEmpty()){
					status="checking sack withdrawal conditions";
					if(!getInventory().contains(emptySackId)
							&& (getBank().contains(emptySackId) && getBank().getItem(emptySackId).getAmount() >= 6)){
						status="gonna withdraw sacks";
						getBank().withdraw(emptySackId, 2);
						sleep(random(300,690));
					}else{
						hasToTrade = true;
					}
					status="checking potato withdrawal conditions";
					if(getAmountInInv(potatoId) < 10
							&& (getBank().contains(potatoId) && getBank().getItem(potatoId).getAmount() >= 50)){
						status="withdrawing potatoes";
						getBank().withdrawAll(potatoId);
						sleep(random(300,690));
					}else{
						hasToTrade = true;
					}
				}else{
					status="depositing all";
					getBank().depositAll();
					sleep(random(650,890));
				}
			}else{
				status="enabling noted mode";
				if(getBank().enableMode(BankMode.WITHDRAW_NOTE)){
					status="withdrawing stuff";
					getBank().withdrawAllButOne(potatoesSackId);
					sleep(random(650,890));
				}
			}
		}else{
			status="interacting with bankchest";
			getObjects().closest(bankChestId).interact("Use");
			sleep(random(900,1200));
		}
	}
	
	private int getAmountInInv(int itemId){
		int n = 0;
		for(Item item : getInventory().getItems())
			if(item != null && item.getId() == itemId)
				n++;
		return n;
	}
	
    private int getY(int startY, int value){
		return startY + value;
	}
	
	private void fillRect(Graphics2D g, Rectangle rect){
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
    
	private boolean needToBreak(){
		return (timeSinceLastBreaked > this.timeBotting) && (timeSinceLastBreaked < (this.timeBotting + this.timeBreaing));
	}
		
    private void doBreak() throws InterruptedException{
		status="Have to break";
		if(getClient().isLoggedIn()){
			resetBreakCheck=true;
			status="logging out to break";
			getLogoutTab().logOut();
			sleep(random(1000,1600));
			this.timeBreakStart = System.currentTimeMillis();
		}
	}
		
	private void breakTimeProcedures(){
		status="break time procedures";
		if(resetBreakCheck){
			resetBreakCheck=false;
			this.timeLastBreaked = System.currentTimeMillis();
			
			this.timeBotting = getBottingTime();
			this.timeBreaing = getBreakingTime();
			
			log("After " + ft(this.timeBotting) + " gonna break for " + ft(this.timeBreaing));
		}
		this.hasStarted = true;
	}
	
	private long getBottingTime(){
		status="getting bottingTime";
		return this.bottingTime + getRandomBreakValue();
	}
	
	private long getBreakingTime(){
		status="getting breakingTime";
		return this.breakingTime + getRandomBreakValue();
	}
	
	private long getRandomBreakValue(){
		status="getting random break value";
		return  ThreadLocalRandom.current().nextLong(-randomizeValue, randomizeValue);
	}
	
    private void mouseListenerStuff(){
    	getBot().addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    private void randomEventStuff(){
    	try {
		    this.bot.getRandomExecutor().registerHook(new RandomBehaviourHook(RandomEvent.AUTO_LOGIN) {
		        @Override
		        public boolean shouldActivate() {
		        	if(hasStarted && needToBreak()){
		        		status="Breaking";
		        		return false;
		        	}else{
		        		status="Loging in";
		        		return super.shouldActivate();
		        	}
		        }
		    });
		} catch (Exception ex) {
		    log("something went wrong");
		}
    }
    
    private void timeStuff(){
    	if(this.startb){
    		this.startb=false;
    		this.timeBegan = System.currentTimeMillis();
    		this.timeReset = timeBegan;
    	}
    	this.timeRan = (System.currentTimeMillis() - this.timeBegan);
    	this.timeSinceReset = (System.currentTimeMillis() - this.timeReset);
    	this.timeSinceLastBreaked = System.currentTimeMillis() - this.timeLastBreaked;
		if (getClient().isLoggedIn()) {
			this.timeBotted = (this.timeSinceReset - this.timeOffline);
		} else {
			this.timeOffline = (this.timeSinceReset - this.timeBotted);
		}
    }
    
    private void resetTime(){
		this.timeReset = System.currentTimeMillis();
		this.timeBotted = 0;
		this.timeOffline = 0;
	}
    
	private String ft(long duration) {
		String res = "";
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(duration));
		if (days == 0L) {
			res = hours + ":" + minutes + ":" + seconds;
		} else {
			res = days + ":" + hours + ":" + minutes + ":" + seconds;
		}
		return res;
	}
}