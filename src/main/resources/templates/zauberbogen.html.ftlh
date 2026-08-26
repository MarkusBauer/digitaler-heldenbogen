<#import "base.html.ftlh" as base>
<#import "blocks/basic.html.ftlh" as basic>
<#import "blocks/talentlist.html.ftlh" as talentlist>
<#import "blocks/zauber.html.ftlh" as zauberblocks>

<@base.html>
    <div class="page page-zauberbogen page-split-if-necessary">
        <div class="sticky-bar">
            <@basic.top_eigenschaften eigenschaften=["Mut","Klugheit","Intuition","Charisma","Fingerfertigkeit","Gewandtheit","Konstitution","Körperkraft","Magieresistenz","Astralenergie","Astralenergie-Regeneration","Karmaenergie"]/>
        </div>

        <@basic.vornachteile filter=renderer.isVorteilMagisch />
        <@basic.sonderfertigkeiten_list filterGroup=renderer.isSfGroupMagisch filter=renderer.sfVisibleRitualPage headers=false />

        <#if zauber?size != 0>
            <@zauberblocks.zauberblock/>

            <#if rituale?size != 0>
                <h2>Rituale <#if held2.getEigenschaft("Karmaenergie") &gt; 0> / Liturgien</#if></h2>
            </#if>
        </#if>

        <#if rituale?size != 0>
            <#if ritualkenntnis.talente?size != 0>
                <@talentlist.talente_list talente=[ritualkenntnis] empty=0/>
            </#if>

            <@zauberblocks.ritualblock/>
        </#if>

        <@basic.footer/>
    </div>

</@base.html>