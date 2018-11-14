#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
  

uniform vec4 g_LightDirection;
uniform vec4 g_LightPosition;
uniform float m_MaxHeight;
uniform float m_WaterLevel;
uniform vec3 m_SunDir;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec2 texCoord1;
varying float intensity;
varying float isWater;

void main(){
    vec3 adjustedPos = inPosition;
    isWater = 0.0;
    
    #ifdef HAS_WATER_LEVEL
      if(adjustedPos.y < m_WaterLevel) {
        //adjustedPos.y = m_WaterLevel; 
        isWater = 1.0;
      }
    #endif
    
    #ifdef HAS_COLORMAP
        #ifdef HAS_MAX_HEIGHT
          #ifdef HAS_WATER_LEVEL
              texCoord1 = vec2(0, (inPosition.y - m_WaterLevel)  / (m_MaxHeight - m_WaterLevel));
          #else
              texCoord1 = vec2(0, inPosition.y / m_MaxHeight);
          #endif
        #else
          texCoord1 = inTexCoord;
        #endif
    #endif     
    
    
    
    //adjustedPos.y = 0.0;
    
    
    gl_Position = g_WorldViewProjectionMatrix * vec4(adjustedPos, 1.0);
        
     // transform normal to camera space and normalize it     
    //vec3 n = normalize(TransformNormal(inNormal)); //normalize(g_NormalMatrix * modelSpaceNorm);
    vec3 n = inNormal;    
 
    #ifdef HAS_SUN_DIR
      vec3 sunDir = m_SunDir;
    #else
      vec3 sunDir = normalize(vec3(1,3,1));
    #endif 
 
    // compute the intensity as the dot product    
    intensity = max(dot(n, sunDir), 0.0);
    
    
}