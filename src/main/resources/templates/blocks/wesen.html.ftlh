<#macro block_wesen current_wesen>
    <div class="wesen">
        <div class="block top-eigenschaften-block">
            <#list ["Mut","Klugheit","Intuition","Charisma","Fingerfertigkeit","Gewandtheit","Konstitution","Körperkraft","Loyalität","Magieresistenz","Geschwindigkeit"] as eigenschaft>
                <#if current_wesen.getEigenschaft(eigenschaft)??>
                    <div class="eigenschaft" data-title="${eigenschaft}">
                        <span class="eigenschaft-name">${short.eigenschaft(eigenschaft)}</span>
                        ${current_wesen.getEigenschaft(eigenschaft)}
                    </div>
                </#if>
            </#list>
        </div>

        <div class="block header-block without-portrait">
            <div class="header-container">
                <h1>${current_wesen.name}</h1>
                <span class="profession">${current_wesen.familie}, ${current_wesen.gattung}</span>
            </div>
            <div class="angaben-container">
                <span class="angabe"><span class="angabe-name">Größe</span><span>${current_wesen.groesse}<span class="unit">cm</span></span></span>
                <span class="angabe"><span class="angabe-name">Gewicht</span><span>${current_wesen.getGewichtInStein()}<span class="unit">Stein</span></span></span>
            </div>
        </div>

        <#if current_wesen.vorteile?has_content>
            <div class="block vorteile-block">
                <h2>Vorteile und Nachteile</h2>
                <#list current_wesen.vorteile as vorteil>
                    <#if vorteil.istVorteil>
                        <span class="vornachteil vorteil" <#if vorteil.kommentar?has_content>data-title="${vorteil.kommentar}"</#if>>${vorteil.displayName};</span>
                    </#if>
                </#list>
                <#list current_wesen.vorteile as nachteil>
                    <#if nachteil.istNachteil>
                        <span class="vornachteil nachteil" <#if nachteil.kommentar?has_content>data-title="${nachteil.kommentar}"</#if>>${nachteil.displayName};</span>
                    </#if>
                </#list>
            </div>
        </#if>

        <div class="row">
            <div class="block sonderfertigkeiten-block col">
                <h2>Sonderfertigkeiten</h2>
                <#list current_wesen.sonderfertigkeiten as sf>
                    <span class="sf" <#if sf.kommentar?has_content>data-title="${sf.kommentar}"</#if>>${sf.name};</span>
                </#list>
            </div>
            <div class="col">
                <div class="block kampf-eigenschaften-block">
                    <#list ["Lebensenergie","Ausdauer","Astralenergie", "Initiative", "Rüstungsschutz", "Gefahrenwert"] as eigenschaft>
                        <#if current_wesen.getEigenschaft(eigenschaft)??>
                            <div class="eigenschaft" data-title="${eigenschaft}">
                                <span class="eigenschaft-name">${short.eigenschaft(eigenschaft)}</span>
                                ${current_wesen.getEigenschaft(eigenschaft)}
                            </div>
                        </#if>
                    </#list>
                </div>

                <div class="block kampf-block">
                    <#list current_wesen.angriffe as angriff>
                        <div class="wesen-attacke">
                        <span class="waffe-name">
                            ${renderer.waffenName1(angriff.name)}
                            <#if renderer.waffenName2(angriff.name)?has_content>
                                <small>${renderer.waffenName2(angriff.name)}</small>
                            </#if>
                        </span>
                            <span class="waffe-wert"><span class="bez" data-title="Attacke">AT</span><#if angriff.at != "0">${angriff.at}</#if></span>
                            <span class="waffe-wert"><span class="bez" data-title="Parade">PA</span><#if angriff.pa != "0">${angriff.pa}</#if></span>
                            <span class="waffe-wert"><span class="bez" data-title="Trefferpunkte">TP</span>${renderer.waffeTP(angriff.tp)}</span>
                            <span class="waffe-wert"><span class="bez" data-title="Distanzklasse">DK</span>${angriff.dk}</span>
                        </div>
                    </#list>
                </div>
            </div>
        </div>


        <#if current_wesen.talente?has_content>
            <div class="block talent-block">
                <div class="talent-gruppe talent-gruppe-wesen">
                    <#list current_wesen.talente as t>
                        <span class="talent-name">${t.name}</span>
                        <#if t.probe?has_content>
                            <span class="talent-probe">${t.probe}</span>
                        </#if>
                        <span class="talent-wert">${t.wert}</span>
                    </#list>
                </div>
            </div>
        </#if>
    </div>
</#macro>