
#ifdef HAS_COLORMAP
  uniform sampler2D m_ColorMap;
#endif
#ifdef HAS_WATERCOLORMAP
  uniform sampler2D m_WaterColorMap;
#endif
#ifdef RENDER_COASTLINE
  uniform bool m_RenderCoastline;
  uniform vec3 m_CoastlineColor;
  #ifdef HAS_COASTLINE_THICKNESS
    uniform float m_CoastlineThickness;
  #endif
#endif

#ifdef HAS_DISCARD_WATER
  uniform bool m_DiscardWater;  
#endif

uniform float m_AmbientLight;

varying vec2 texCoord1;
varying float intensity;
varying float isWater;

void main(){

    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
      if(isWater != 1.0) {
        color *= texture2D(m_ColorMap, texCoord1);
      }     
    #endif
   
    #ifdef HAS_WATERCOLORMAP
      vec4 wcol  = texture2D(m_WaterColorMap, texCoord1);
    #else     
      vec4 wcol = vec4(0.0,0.0,0.7,1.0);                             
    #endif

    color = mix(wcol, color, 1.0 - isWater);
    //color *= intensity;
    color *= m_AmbientLight + (intensity * (1.0 - m_AmbientLight));
    
        
    #ifdef RENDER_COASTLINE        
      if(m_RenderCoastline) {                  
         #ifdef HAS_COASTLINE_THICKNESS
           float thinness = 1.0 - m_CoastlineThickness;
         #else
           float thinness = 0.001;           
         #endif  
         thinness = clamp(thinness, 0.001, 0.999);       
         if(isWater < 1.0 - thinness && isWater > thinness) {
           color = vec4(m_CoastlineColor,1.0);
         } 
         //else { discard; }
      }    
    #endif
    
    #ifdef HAS_DISCARD_WATER    
      if(m_DiscardWater && isWater >= 0.99) {
        discard; 
      }    
    #endif
    
    color.w = 1.0;

    gl_FragColor = color;
}