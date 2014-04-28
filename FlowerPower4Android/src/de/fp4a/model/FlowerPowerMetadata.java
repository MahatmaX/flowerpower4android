package de.fp4a.model;

import java.io.Serializable;

public class FlowerPowerMetadata implements Serializable
{
	private static final long serialVersionUID = -9111280335181205848L;

	public enum FlowerPowerColors {UNKNOWN, BROWN, BLUE, GREEN};
	
	// static fields, these normally do not change
	private String systemId 		= "";
	private String modelNr 			= "";
	private String serialNr 		= "";
	private String firmwareRevision	= "";
	private String hardwareRevision	= "";
	private String softwareRevision	= "";
	private String manufacturerName	= "";
	private String certData 		= ""; // IEEE 11073-20601 regulatory certification data list
	private String pnpId 			= "";
	private String friendlyName 	= "";
	private FlowerPowerColors color	= FlowerPowerColors.UNKNOWN; // one of enum 'FlowerPowerColors'
	
	private long metadataTimestamp;
	
	public FlowerPowerMetadata()
	{
		
	}
	
	public String getSystemId()
	{
		return systemId;
	}

	public void setSystemId(String systemId)
	{
		this.systemId = systemId;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getModelNr()
	{
		return modelNr;
	}

	public void setModelNr(String modelNr)
	{
		this.modelNr = modelNr;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getSerialNr()
	{
		return serialNr;
	}

	public void setSerialNr(String serialNr)
	{
		this.serialNr = serialNr;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getFirmwareRevision()
	{
		return firmwareRevision;
	}

	public void setFirmwareRevision(String firmwareRevision)
	{
		this.firmwareRevision = firmwareRevision;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getHardwareRevision()
	{
		return hardwareRevision;
	}

	public void setHardwareRevision(String hardwareRevision)
	{
		this.hardwareRevision = hardwareRevision;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getSoftwareRevision()
	{
		return softwareRevision;
	}

	public void setSoftwareRevision(String softwareRevision)
	{
		this.softwareRevision = softwareRevision;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getManufacturerName()
	{
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getCertData()
	{
		return certData;
	}

	public void setCertData(String certData)
	{
		this.certData = certData;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getPnpId()
	{
		return pnpId;
	}

	public void setPnpId(String pnpId)
	{
		this.pnpId = pnpId;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public String getFriendlyName()
	{
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public FlowerPowerColors getColor()
	{
		return color;
	}

	public void setColor(FlowerPowerColors color)
	{
		this.color = color;
		setMetadataTimestamp(System.currentTimeMillis());
	}

	public long getMetadataTimestamp()
	{
		return metadataTimestamp;
	}

	private void setMetadataTimestamp(long metadataUpdateTimestamp)
	{
		this.metadataTimestamp = metadataUpdateTimestamp;
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowerPowerMetadata other = (FlowerPowerMetadata) obj;
		if (systemId == null)
		{
			if (other.systemId != null)
				return false;
		}
		else if (!systemId.equals(other.systemId))
			return false;
		return true;
	}
	
	public String toString()
	{
		return "FlowerPowerMetadata [systemId=" + systemId + ", modelNr=" + modelNr + ", serialNr=" + serialNr + ", firmwareRevision=" + firmwareRevision + ", hardwareRevision=" + hardwareRevision + ", softwareRevision=" + softwareRevision + ", manufacturerName=" + manufacturerName + ", certData=" + certData + ", pnpId=" + pnpId + ", friendlyName=" + friendlyName + ", color=" + color + ", metadataUpdateTimestamp=" + metadataTimestamp + "]";
	}
}
