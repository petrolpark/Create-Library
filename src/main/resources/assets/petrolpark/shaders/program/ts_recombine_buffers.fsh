#version 150

uniform sampler2D DiffuseSampler;

uniform sampler2D EntityCutoutNoCullSampler;
uniform sampler2D EntityCutoutNoCullDepthSampler;
uniform sampler2D EntityTranslucentSampler;
uniform sampler2D EntityTranslucentDepthSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D HandDepthSampler;
uniform sampler2D TransitionSampler;

uniform float EffectFactor;

in vec2 texCoord;

out vec4 fragColor;

void main(){
    float diffuseDepth = texture(DiffuseDepthSampler, texCoord).r;
    float entityCutoutDepth = texture(EntityCutoutNoCullDepthSampler, texCoord).r;
    float entityTranslucentDepth = texture(EntityTranslucentDepthSampler, texCoord).r;
    float handDepth = texture(HandDepthSampler, texCoord).r;

    vec4 diffuseColor = texture(DiffuseSampler, texCoord);
    vec4 entityCutoutColor = texture(EntityCutoutNoCullSampler, texCoord);
    vec4 entityTranslucentColor = texture(EntityTranslucentSampler, texCoord);

    if ((diffuseDepth < entityCutoutDepth && diffuseDepth < entityTranslucentDepth)
    || (handDepth < entityTranslucentDepth)
    || (entityCutoutColor.a < .1 && entityTranslucentColor.a < .1)) {
        fragColor = diffuseColor;
        return;
    }

    if (entityCutoutDepth < entityTranslucentDepth) {
        fragColor = entityCutoutColor;
        return;
    } else {
        fragColor = entityTranslucentColor;
        return;
    }
}