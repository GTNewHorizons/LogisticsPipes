package logisticspipes.utils.item;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;

public class EzNBT {

    private static EzNBT NOTHING = new EzNBT((NBTBase) null);

    protected final Optional<NBTBase> base;
    protected final Kind kind;

    public EzNBT(final NBTBase base) {
        this.base = Optional.ofNullable(base);
        this.kind = Kind.determine(base)
                .orElseThrow(() -> new IllegalArgumentException("Unknow NBT Tag Type: " + base.getId()));
    }

    public EzNBT(final ItemStack stack) {
        this(stack.getTagCompound());
    }

    public EzNBT get(final String key) {
        if (this.kind != Kind.COMPOUND) {
            return EzNBT.NOTHING;
        }
        return new EzNBT(asCompound().get().getTag(key));
    }

    public Optional<String> valueString() {
        if (this.kind != Kind.STRING) {
            return Optional.empty();
        }
        return Optional.ofNullable(asString().get().func_150285_a_());
    }

    public Optional<String> getString(final String key) {
        if (this.kind != Kind.COMPOUND) {
            return Optional.empty();
        }
        final NBTTagCompound compound = asCompound().get();
        if (!compound.hasKey(key)) {
            // This special key is here becasue when using the NBTTagCompound.getString()
            // directly there is no way to determine if the key is absent or set to an empty string
            return Optional.empty();
        }
        return Optional.of(compound.getString(key));
    }

    public Optional<Integer> valueInteger() {
        if (this.kind != Kind.STRING) {
            return Optional.empty();
        }
        return Optional.ofNullable(asInteger().get().func_150287_d());
    }

    public Optional<Integer> getInteger(final String key) {
        if (this.kind != Kind.COMPOUND) {
            return Optional.empty();
        }
        final NBTTagCompound compound = asCompound().get();
        if (!compound.hasKey(key)) {
            // This special key is here becasue when using the NBTTagCompound.getInteger()
            // directly there is no way to determine if the key is absent or set to 0
            return Optional.empty();
        }
        return Optional.of(compound.getInteger(key));
    }

    public boolean hasAll(final String... keys) {
        if (this.kind != Kind.COMPOUND || keys.length == 0) {
            return false;
        }
        final NBTTagCompound self = this.asCompound().get();
        for (String tested : keys) {
            if (!self.hasKey(tested)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAny(final String... keys) {
        if (this.kind != Kind.COMPOUND || keys.length == 0) {
            return false;
        }
        final NBTTagCompound self = this.asCompound().get();
        for (String tested : keys) {
            if (self.hasKey(tested)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPresent() {
        return this.base.isPresent();
    }

    public boolean isEmpty() {
        return !isPresent();
    }

    public void ifPresent(final Consumer<NBTTagCompound> fn) {
        if (this.isPresent()) {
            if (this.kind != Kind.COMPOUND) {
                final String msg = String.format(
                        "The tag is present but has an incorrect type. Expected: {}, found: {}",
                        Kind.COMPOUND.name(),
                        this.kind.name());
                throw new IllegalArgumentException(msg);
            }
            fn.accept((NBTTagCompound) this.base.get());
        }
    }

    public boolean hasKey(final String key) {
        if (this.kind != Kind.COMPOUND) {
            return false;
        }
        return asCompound().get().hasKey(key);
    }

    public Optional<NBTTagCompound> asCompound() {
        if (this.kind != Kind.COMPOUND) {
            return Optional.empty();
        }
        return this.base.map(it -> (NBTTagCompound) it);
    }

    public Optional<NBTTagString> asString() {
        if (this.kind != Kind.STRING) {
            return Optional.empty();
        }
        return this.base.map(it -> (NBTTagString) it);
    }

    public Optional<NBTTagInt> asInteger() {
        if (this.kind != Kind.INT) {
            return Optional.empty();
        }
        return this.base.map(it -> (NBTTagInt) it);
    }

    public enum Kind {

        NOTHING(-1, "NOTHING"),
        END(0, NBTBase.NBTTypes[0]),
        BYTE(1, NBTBase.NBTTypes[1]),
        SHORT(2, NBTBase.NBTTypes[2]),
        INT(3, NBTBase.NBTTypes[3]),
        LONG(4, NBTBase.NBTTypes[4]),
        FLOAT(5, NBTBase.NBTTypes[5]),
        DOUBLE(6, NBTBase.NBTTypes[6]),
        BYTE_ARRAY(7, NBTBase.NBTTypes[7]),
        STRING(8, NBTBase.NBTTypes[8]),
        LIST(9, NBTBase.NBTTypes[9]),
        COMPOUND(10, NBTBase.NBTTypes[10]),
        INT_ARRAY(11, NBTBase.NBTTypes[11]);

        private int id;
        private String _name;

        static Optional<Kind> determine(final NBTBase base) {
            if (base == null) {
                return Optional.of(NOTHING);
            }
            final int baseId = base.getId();
            return Stream.of(Kind.values()).filter(it -> it.getId() == baseId).findAny();
        }

        Kind(final int id, final String name) {
            this.id = id;
            this._name = name;
        }

        int getId() {
            return this.id;
        }

        String getName() {
            return this._name;
        }
    }
}
