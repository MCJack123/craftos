/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.media.items;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import com.jackmacwindows.craftos.World;

import javax.annotation.Nonnull;

// An implementation of IMedia for ItemRecord's
public class RecordMedia implements IMedia
{
    public RecordMedia()
    {
    }
     
    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return getAudioTitle( stack );
    }
    
    @Override
    public boolean setLabel( @Nonnull ItemStack stack, String label )
    {
        return false;
    }
    
    @Override
    public String getAudioTitle( @Nonnull ItemStack stack )
    {
        return ComputerCraft.getRecordInfo( stack );
    }
    
    @Override
    public SoundEvent getAudio( @Nonnull ItemStack stack )
    {
        ItemRecord itemRecord = (ItemRecord)stack.getItem();
        return itemRecord.sound;
    }
    
    @Override
    public IMount createDataMount( @Nonnull ItemStack stack, @Nonnull World world )
    {
        return null;
    }
}
