package com.github.tukenuke.tuske.extensions.we;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Registry;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExprWorldEditPasteSchematic extends SimpleExpression<Boolean> {
    static {
        Registry.newSimple(ExprWorldEditPasteSchematic.class, "paste schematic %string% at %location%");
        System.out.println("Load paste schematic");
    }

    private Expression<String> pathExpr;
    private Expression<Location> locationExpr;

    @Override
    public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        pathExpr = (Expression<String>) arg[0];
        locationExpr = (Expression<Location>) arg[1];
        return true;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "paste schematic " + pathExpr.toString(arg0, arg1) + " at " + locationExpr.toString(arg0, arg1);
    }

    @Override
    @Nullable
    protected Boolean[] get(Event e) {
        String path = pathExpr.getSingle(e);
        Location location = locationExpr.getSingle(e);
        if (path != null && location != null) {
            Clipboard clipboard = null;
            File file = new File(path);
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            } catch (IOException ex) {
                ex.printStackTrace();
                return new Boolean[]{false};
            }
            if (clipboard == null)
                return new Boolean[]{false};

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException ex) {
                ex.printStackTrace();
                return new Boolean[]{false};
            }
            return new Boolean[]{true};
        }
        return new Boolean[]{false};
    }

}
