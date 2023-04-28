package logisticspipes.proxy.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.minecraft.util.IIcon;

import logisticspipes.proxy.object3d.interfaces.*;
import logisticspipes.proxy.object3d.operation.LPScale;

public interface ICCLProxy {

    IIconTransformation createIconTransformer(IIcon registerIcon);

    IRenderState getRenderState();

    Map<String, IModel3D> parseObjModels(InputStream resourceAsStream, int i, LPScale scale) throws IOException;

    Object getRotation(int i, int j);

    Object getScale(double d, double e, double f);

    Object getScale(double d);

    ITranslation getTranslation(double d, double e, double f);

    ITranslation getTranslation(IVec3 min);

    Object getUVScale(double i, double d);

    Object getUVTranslation(float i, float f);

    Object getUVTransformationList(I3DOperation[] uvTranslation);

    IModel3D wrapModel(Object model);

    boolean isActivated();
}
