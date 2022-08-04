namespace CSNamedPipeServer;

public class Npc : Entity
{
    public string npcClass;
}

public class NpcWithWeapon : Npc
{
    public string primary;
    public string secondary;
}

public class NpcTitan : Npc
{
    public string titanClass;
}