package davinci;

import java.util.ArrayList;

import gui.Home;

public class DeviceList
{
    private ArrayList<Device> devices;

    public DeviceList()
    {
        this.devices = new ArrayList<>();
    }

    public void add(final Device input)
    {
        this.devices.add(input);
    }

    public int getIndex(Device input)
    {
        return devices.indexOf(input);
    }

    public boolean contains(Device input)
    {
        if (this.devices == null || input == null)
        {
            return false;
        } else
        {
            if (this.devices.isEmpty())
            {
                return false;
            } else
            {
                for (final Device d : this.devices)
                {
                    return d.ip().equals(input.ip());
                }
            }
        }
        return false;
    }

    public ArrayList<Device> get()
    {
        return this.devices;
    }

    public ArrayList<Device> getDevices()
    {
        return this.devices;
    }

    public Device get(int index)
    {
        return this.devices.get(index);
    }

    public void insert(
        int index,
        Device input)
    {
        this.devices.add(index, input);
    }

    public void remove(int index)
    {
        this.devices.remove(index);
    }

    public int size() // 0 based index
    {
        return this.devices.size();
    }

    public boolean isEmpty()
    {
        return this.devices.size() == 0;
    }
}
