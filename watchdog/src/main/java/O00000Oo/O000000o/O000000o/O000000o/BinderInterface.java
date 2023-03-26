package O00000Oo.O000000o.O000000o.O000000o;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 这块从调用来看，是发起 bindService() 过程中，返回给客户端的 binder
 * 但是客户但，并没有在 ServiceConnection 中获取binder。
 * 像是由 BinderInterface.aidl 生成的
 */
public interface BinderInterface extends IInterface {
    public static abstract class Stub extends Binder implements BinderInterface {
        public Stub() {
            //当前接口关联的 Binder 对象。
            this.attachInterface(this, "fake");
        }

        @Override  // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override  // android.os.Binder
        public boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) {
            // 1 是个关键点
            if (code == 1) {
                data.enforceInterface("fake");
                String v4 = ServiceBinder.class.getSimpleName();
                reply.writeNoException();
                reply.writeString(v4);
                return true;
            }

            if (code != 0x5F4E5446) {
                try {
                    return super.onTransact(code, data, reply, flags);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            reply.writeString("fake");
            return true;
        }
    }

}

