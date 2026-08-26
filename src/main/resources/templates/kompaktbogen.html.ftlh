<#import "base.html.ftlh" as base>
<#import "blocks/basic.html.ftlh" as basic>
<#import "blocks/kampf.html.ftlh" as kampf>
<#import "blocks/profile.html.ftlh" as profile>
<#import "blocks/talentlist.html.ftlh" as talentlist>
<@base.html>
    <div class="page page-kompaktbogen">
        <div class="sticky-bar">
            <@basic.top_eigenschaften eigenschaften=["Mut","Klugheit","Intuition","Charisma","Fingerfertigkeit","Gewandtheit","Konstitution","Körperkraft","Sozialstatus","Magieresistenz","Geschwindigkeit"]/>
        </div>
        <@profile.profile/>
        <@basic.vornachteile/>

        <div class="row">
            <@basic.sonderfertigkeiten_list class="col" filter=renderer.sfVisibleFirstPage/>

            <div class="col">
                <@kampf.eigenschaften/>
                <@kampf.waffen/>
            </div>
        </div>

        <@talentlist.talente_list/>

        <@basic.notizen/>

        <@basic.footer/>
    </div>

</@base.html>