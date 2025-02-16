/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.ext;

import com.mojang.blaze3d.pipeline.RenderTarget;

import javax.annotation.Nullable;

public interface MinecraftExt {
    void setMainRenderTargetOverride(@Nullable RenderTarget renderTarget);
}
