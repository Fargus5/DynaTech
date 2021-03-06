package me.profelements.dynatech.items.electric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.profelements.dynatech.items.electric.abstracts.AMachine;

public class AntigravityBubble extends AMachine {

    private Set<UUID> enabledPlayers = new HashSet<>();

    private static int[] BORDER = new int[] { 1, 2, 6, 7, 9, 10, 11, 15, 16, 17, 19, 20, 24, 25 };
    private static int[] BORDER_IN = new int[] { 3, 4, 5, 12, 14, 21, 22, 23 };
    private static int[] BORDER_OUT = new int[] { 0, 8, 18, 26 };

    public AntigravityBubble(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

    }

    @Override
    public void blockExtras(Block b) {
        clearFlightFromPlayer(b);
        enabledPlayers.clear();
    }

    private void clearFlightFromPlayer(Block block) {
        Location l = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        for (Player p : l.getWorld().getPlayers()) {
            if (enabledPlayers.contains(p.getUniqueId())) {
                p.setFlying(false);
                p.setAllowFlight(false);
                p.setFallDistance(0.0f);
            }
        }
    }

    @Override
    public void tick(Block b) {
        doFlightIfAvailable(b);
    }

    protected void doFlightIfAvailable(Block block) {
        if (getCharge(block.getLocation()) < getEnergyConsumption()) {
        return;
        }

        for (Player p : block.getWorld().getPlayers()) {
            double distance = block.getLocation().distance(p.getLocation());
            Set<UUID> plrsToRemove = new HashSet<>();

            if (!enabledPlayers.contains(p.getUniqueId()) && distance < 22.5 && !p.getAllowFlight()) {
                    p.setAllowFlight(true);
                    enabledPlayers.add(p.getUniqueId());
                    removeCharge(block.getLocation(), getEnergyConsumption());
                } 
            
            for (UUID playerUUID : enabledPlayers) {
                Player plr = Bukkit.getPlayer(playerUUID);
                double distance2 = block.getLocation().distance(plr.getLocation());
                if (distance2 >= 22.5) {
                    plrsToRemove.add(plr.getUniqueId());
                }
            }

            for(UUID playerToRemove : plrsToRemove) {
                Player plyr = Bukkit.getPlayer(playerToRemove);
                plyr.setFlying(false);
                plyr.setAllowFlight(false);
                plyr.setFallDistance(0.0f);
                enabledPlayers.remove(playerToRemove);
            }
            plrsToRemove.clear();
        }        
    };

    @Override
    public boolean isGraphical() {
        return false;
    }
    
    @Override
    public String getMachineIdentifier() {
        return "ANTIGRAVITY_BUBBLE";
    }


    @Override
    public List<int[]> getBorders() {
        List<int[]> borders = new ArrayList<>();
        borders.add(BORDER);
        borders.add(BORDER_IN);
        borders.add(BORDER_OUT);
        
        return borders;
    }
    
    @Override
    public int[] getInputSlots() {
        return new int[] {13};
    }
    @Override
    public int[] getOutputSlots() {
        return new int[] {13};
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.DRAGON_EGG);
    }
    
    @Override
    public int getProgressBarSlot() {
        return 4;
    }
    
}
