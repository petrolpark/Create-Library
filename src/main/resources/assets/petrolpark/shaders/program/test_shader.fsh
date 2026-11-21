#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MainSampler;

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

    vec4 mainColor = texture(MainSampler, texCoord);
    vec4 diffuseColor = texture(DiffuseSampler, texCoord);
    vec4 entityCutoutColor = texture(EntityCutoutNoCullSampler, texCoord);
    vec4 entityTranslucentColor = texture(EntityTranslucentSampler, texCoord);

    bool flag = bool(((texture(TransitionSampler, texCoord).r) < (cos(pow((EffectFactor - .5)/.45, 4)))));

    if ((diffuseDepth < entityCutoutDepth && diffuseDepth < entityTranslucentDepth)
    || (handDepth < entityTranslucentDepth)
    || (entityCutoutColor.a < .1 && entityTranslucentColor.a < .1)) {
        if (flag) {
            fragColor = vec4(vec3(mainColor.r), 1.0);
            return;
        }
    }

    if (flag) {
        if (entityCutoutDepth <= entityTranslucentDepth) {
            fragColor = vec4(entityCutoutColor.r, 0.0, 0.0, entityCutoutColor.a);
            return;
        } else {
            fragColor = vec4(entityTranslucentColor.r, 0.0, 0.0, entityTranslucentColor.a);
            return;
        }
    }

    fragColor = diffuseColor;
}